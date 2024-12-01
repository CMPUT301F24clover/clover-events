package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;


public class TestViewProfile {

    @Rule
    public ActivityScenarioRule<LoginActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    // we first have to login to an account
    public void navigationToViewProfile(){
        // fill out sign in fields
        onView(withId(R.id.username_editText)).perform(ViewActions.typeText("testview123"));
        onView(withId(R.id.password_editText)).perform(ViewActions.typeText("test123"));

        //click sign in
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // check to see if we see the bottom navigation bar meaning were were able to sign in
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        // click the profile icon on the navigation bar to go to register profile
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        // if we see button3 (logout button) it means were on the view page
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
    }

    @Test
    public void testProfileView(){
        navigationToViewProfile();
        // make sure the fields match the ones in the db
        onView(withId(R.id.nameField)).check(matches(withText("test view")));
        onView(withId(R.id.emailField)).check(matches(withText("testview@gmail.com")));
        onView(withId(R.id.phoneField)).check(matches(withText("123-432-2345")));
    }
}
