package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantSignUpTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    public void navigateToSignUp(){
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());
    }

    @Test
    public void testEmptyUsernamePasswordFields() throws InterruptedException{
        navigateToSignUp();
        Thread.sleep(2000);
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));

        onView(withId(R.id.SignUpUsernameInput)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));


        onView(withId(R.id.SignUpUsernameInput)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.SignUpPasswordInput)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));

    }

    @Test
    public void testInvalidPassword(){
        onView(withId(R.id.SignUpUsernameInput)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.SignUpPasswordInput)).perform(replaceText("clover"), closeSoftKeyboard());
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));

    }
}
