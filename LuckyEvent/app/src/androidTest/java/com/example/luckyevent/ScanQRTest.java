package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ScanQRTest tests the navigation to the scan QR screen and the event details screen.
 * It also tests if the user can join and leave the waiting list.
 * User stories being tested: US 01.01.01, US 01.01.02, US 01.06.01, US 01.06.02, and US 01.08.01
 * This test requires manual scanning as there is no-way to pass a hash.
 * @author Tola
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScanQRTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testScanQRFlow() {
        // Clear focus first
        onView(withId(R.id.usernameInput)).perform(ViewActions.closeSoftKeyboard());

        // Login
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("ayesha.b"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(ViewActions.click());

        // Wait for login and activity transition
        try {
            Thread.sleep(3000); // Increased wait time for activity transition
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now check if we're in the main activity by looking for the camera item first
        onView(withId(R.id.camera_item)).check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Click the camera item to go to scan QR screen
        onView(withId(R.id.camera_item)).perform(ViewActions.click());

        // Wait for navigation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify scan button is visible
        onView(withId(R.id.button_startScanning))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Scan QR screen did not load properly. Error: " + error.getMessage());
                })
                .check(matches(isDisplayed()));

        // Click scan button to open camera
        onView(withId(R.id.button_startScanning)).perform(ViewActions.click());

        // Wait longer for manual QR scanning and activity transition
        try {
            // Give user 10 seconds to scan
            Thread.sleep(10000);

            // Now verify event details screen elements are visible (aka join waitlist button)
            onView(withId(R.id.button_join)).check(matches(isDisplayed()));

            // Click join waitlist button
            onView(withId(R.id.button_join)).perform(ViewActions.click());

            // Wait for activity transition
            Thread.sleep(2000);
            // Handle the alert dialog - click "Join"
            onView(withText("Join"))
                    .inRoot(isDialog())
                    .perform(ViewActions.click());

            Thread.sleep(3000);

            // Check if leave waitlist button is visible
            onView(withId(R.id.button_leave)).check(matches(isDisplayed()));
            Thread.sleep(2000);

            // Click leave waitlist button
            onView(withId(R.id.button_leave)).perform(ViewActions.click());

            // Handle the alert dialog - click "Yes"
            onView(withText("Yes"))
                    .inRoot(isDialog()) // Important! This tells Espresso to look for the button in a dialog
                    .perform(ViewActions.click());

            // Wait for the dialog to dismiss and action to complete
            Thread.sleep(2000);

            // Verify we're back to the join button state
            onView(withId(R.id.button_join)).check(matches(isDisplayed()));

        } catch (InterruptedException e) {
            throw new RuntimeException("Test interrupted while waiting for QR scan", e);
        }
    }
}