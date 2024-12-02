package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.activities.LoginActivity;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;

public class DeviceRegistrationTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testDeviceRegistration() throws InterruptedException{
        // Register into the app as a guest
        onView(withId(R.id.continue_as_guest_button)).perform(click());
        onView(withId(R.id.RegisterButton)).perform(click());

        // The test passes if we have successfully navigated to the entrant homepage
        Thread.sleep(5000);
        onView(withId(R.id.mainContentCard)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeviceRegistrationProfile() throws  InterruptedException{
        // Register into the app as a guest
        onView(withId(R.id.continue_as_guest_button)).perform(click());
        onView(withId(R.id.RegisterButton)).perform(click());

        // Check if we have successfully navigated to the entrant homepage
        Thread.sleep(5000);
        onView(withId(R.id.mainContentCard)).check(matches(isDisplayed()));

        // Check if the bottom navigation bar is displayed and click on the profile item
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());

        try {
            // If the register profile screen is displayed create a new profile
            onView(withId(R.id.SetUpText))
                    .check(matches(isDisplayed()));
            onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.typeText("Ferry Doe"));
            onView(withId(R.id.emailEditText)).perform(replaceText("Doe@gmail.com"), closeSoftKeyboard());
            onView(withId(R.id.RegisterButton)).perform(ViewActions.click());

            Thread.sleep(2000);

            // The test passes if we have successfully navigated to the view profile screen
            onView(withId(R.id.checkBox))
                    .check(matches(isDisplayed()));

        } catch (NoMatchingViewException | AssertionFailedError e) {
            System.out.println("Element is not displayed!");

            onView(withId(R.id.checkBox))
                    .check(matches(isDisplayed()));
        }
    }
}
