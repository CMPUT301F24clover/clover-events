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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerSignUpTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    public void navigateToSignUp(){
        onView(withId(R.id.sign_in_as_organizer_button)).perform(ViewActions.click());
        onView(withId(R.id.sign_up_button)).perform(ViewActions.click());
    }

    @Test
    public void testEmptyUsernamePasswordFields() throws InterruptedException{
        navigateToSignUp();
        // Check if we have successfully navigated to the organizer sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));
        // Try signing up without a password provided
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());

        // Check if we are still in the organizer sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));

        //Try singing up without a username provided
        onView(withId(R.id.username_editText)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());

        // Check if we are still in the organizer sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));

    }
    @Test
    public void testInvalidPassword(){
        navigateToSignUp();
        // Check if we have successfully navigated to the organizer sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));
      
        //Try to signup with a valid username and an invalid password
        onView(withId(R.id.username_editText)).perform(replaceText("JerryGo21"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());

        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));
    }


    @Test
     public void testTakenUsername(){
        navigateToSignUp();
       
        //Try to signup with a username that is already taken
        onView(withId(R.id.username_editText)).perform(replaceText("JerryDpe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.organization_name_editText)).perform(replaceText("Doe.inc"), closeSoftKeyboard());
        onView(withId(R.id.facility_code_editText)).perform(replaceText("AbSK@!"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());

        // Check if we are still in the organizer sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));

    }




}
