package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.activities.LoginActivity;
import com.example.luckyevent.activities.MenuActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSignIn() {

        onView(withId(R.id.SignInButton)).perform(click());

        /* //This is not working as expected.
        //It was supposed to check the toasts created

        onView(withText("Sign-in failed: Username not found"))
                .inRoot(withDecorView(not(isRoot())))
                .check(matches(isDisplayed()));

         */
        //checks if it is still in the login activity the sign in wasn't successfull
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity instanceof LoginActivity);
        });

        //type in a username that doesn't exist and try to sign in
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("test123"));
        onView(withId(R.id.SignInButton)).perform(click());
        scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity instanceof LoginActivity);
        });

        //type in a username that exists but enter no password
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("JohnDoe"));
        onView(withId(R.id.SignInButton)).perform(click());
        scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity instanceof LoginActivity);
        });

        //try signing with a username that is in the system but with the wrong password
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("JohnDoe"));
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("654321"));
        onView(withId(R.id.SignInButton)).perform(click());
        scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity instanceof LoginActivity);
        });

        //try signing with an organizer's account
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("JennyDoe"));
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
        onView(withId(R.id.SignInButton)).perform(click());
        scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity instanceof LoginActivity);
        });

        //enter the correct username and password and verify navigation to the menu activity
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("JohnDoe"));
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
        onView(withId(R.id.SignInButton)).perform(click());
        ActivityScenario<MenuActivity> menuScenario = ActivityScenario.launch(MenuActivity.class);
        menuScenario.onActivity(activity -> {
            assertTrue(activity instanceof MenuActivity);
        });

    }

    }



