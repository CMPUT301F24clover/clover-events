package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.activities.LoginActivity;
import com.example.luckyevent.activities.MenuActivity;
import com.example.luckyevent.activities.OrganizerSignInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerSignInActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testOrganizerSignIn() {
        //check if it can successfully navigate to the menu activity
        onView(withId(R.id.OrganizerText)).perform(click());
        ActivityScenario<OrganizerSignInActivity> organizerSignInScenario = ActivityScenario.launch(OrganizerSignInActivity.class);
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //try signing in with empty username and password fields an
        onView(withId(R.id.SignInButton)).perform(click());
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //try signing in with a username that doesn't exists
        onView(withId(R.id.usernameInput)).perform(replaceText("test123"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //type in a username that exists but enter no password
        onView(withId(R.id.usernameInput)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //try signing with a username that is in the system but with the wrong password
        onView(withId(R.id.usernameInput)).perform(replaceText("JennyDoe"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("654321"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //try signing with an entrant's account
        onView(withId(R.id.usernameInput)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });

        //sign in with an organizers account
        onView(withId(R.id.usernameInput)).perform(replaceText("JennyDoe"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        ActivityScenario<MenuActivity> menuScenario = ActivityScenario.launch(MenuActivity.class);
        organizerSignInScenario.onActivity(activity -> {
            assertTrue(activity instanceof OrganizerSignInActivity);
        });
    }
}
