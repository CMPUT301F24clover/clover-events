package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

public class DisableNotificationsTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    public void navigateToProfile() throws InterruptedException{
        // Enter a valid username and password and click on the button to sign in
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        Thread.sleep(5000);

        // Check if we have successfully navigated to the homepage
        onView(withId(R.id.mainContentCard)).check(matches(isDisplayed()));

        // Check if the bottom navigation bar is displayed and click on the profile item
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());

        Thread.sleep(2000);
    }

    @Test
    public void testDisableNotifications() throws InterruptedException{
        navigateToProfile();

        // Click on the enable notifications check box
        onView(withId(R.id.checkBox)).perform(ViewActions.click());

        // Navigate to the notifications screen
        onView(withId(R.id.imageButton)).perform(ViewActions.click());
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_item)).perform(ViewActions.click());
        // Check if the notifications list is empty
        onView(withId(R.id.text_emptyList)).check(matches(isDisplayed()));

        // Navigate back to the the view profile screen and check the check box
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        onView(withId(R.id.checkBox)).perform(ViewActions.click());
        onView(withId(R.id.imageButton)).perform(ViewActions.click());
        Thread.sleep(4000);

        // The test passes if we are still in the view profile screen
        onView(withId(R.id.text_emptyList)).check(matches(isDisplayed()));
    }
}
