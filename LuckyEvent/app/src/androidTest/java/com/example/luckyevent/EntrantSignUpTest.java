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

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantSignUpTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    public void navigateToSignUp(){
        onView(withId(R.id.sign_up_button)).perform(ViewActions.click());
    }

    @Test
    public void testEmptyUsernamePasswordFields() throws InterruptedException{
        navigateToSignUp();
        Thread.sleep(2000);

        //
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Use case where the user signs up without a password
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());
        // The test passes if the textview2 ("SignUp") is still displayed
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Use case where the user signs up without a username
        onView(withId(R.id.username_editText)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());
        // The test passes if the textview2 ("SignUp") is still displayed
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

    }

    @Test
    public void testInvalidPassword(){

        navigateToSignUp();
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover"), closeSoftKeyboard());
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpNavigation() throws InterruptedException{
        navigateToSignUp();

        Random rand = new Random();
        int generatedNumber = rand.nextInt(1000);

        // Create a new user to test the screen navigation
        onView(withId(R.id.username_editText)).perform(replaceText("DummyUser" + Integer.toString(generatedNumber)), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.firstname_editText)).perform(replaceText("Dummy"), closeSoftKeyboard());
        onView(withId(R.id.lastname_editText)).perform(replaceText("User"), closeSoftKeyboard());
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());

        // Wait for the homepage to load in
        Thread.sleep(5000);

        // The test passes if the toolbarTitle ("CommunityCenter") is displayed
        onView(withId(R.id.toolbarTitle)).check(matches(isDisplayed()));
    }


}
