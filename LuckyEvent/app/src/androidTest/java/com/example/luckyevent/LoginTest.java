package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testEmptyUsernamePasswordFields() {
        // Test if we are able to sign in without username and password fields
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Type in a username that doesn't exist and try to sign in
        onView(withId(R.id.username_editText)).perform(replaceText("test123"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Type in a username that exists but enter no password
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));
    }

    @Test
    public void testInvalidFields() throws InterruptedException{
        // Try signing with a username that is in the system but with the wrong password
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("654321"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Try signing with an organizer's account
        onView(withId(R.id.username_editText)).perform(replaceText("JerryDpe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        //enter the correct username and password and verify navigation to the menu activity
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.mainContentCard)).check(matches(isDisplayed()));
    }

}



