package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.example.luckyevent.activities.EditProfileActivity;
import com.example.luckyevent.activities.LoginActivity;
import com.example.luckyevent.activities.MenuActivity;
import com.example.luckyevent.activities.OrganizerSignInActivity;
import com.example.luckyevent.activities.ViewProfileActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testEditProfile() throws InterruptedException{
        onView(withId(R.id.usernameInput)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(click());

        //navigate to the editprofile activity and test edits with empty strings
        onView(withId(R.id.editButton)).perform(click());
        onView(withId(R.id.nameEditText)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.emailEditText)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        ActivityScenario<EditProfileActivity> editProfileScenario = ActivityScenario.launch(EditProfileActivity.class);
        editProfileScenario.onActivity(activity -> {
            assertTrue(activity instanceof EditProfileActivity);
        });

        //try to make edits with and invalid name
        onView(withId(R.id.nameEditText)).perform(replaceText("Johnathan"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText)).perform(replaceText("Johhny@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        editProfileScenario.onActivity(activity -> {
            assertTrue(activity instanceof EditProfileActivity);
        });


        //try to make edits and see if they have
        Thread.sleep(10000);
        onView(withId(R.id.nameEditText)).perform(replaceText("Johnathan Doeler"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText)).perform(replaceText("Johhny@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        onView(withId(R.id.nameField)).check(matches(withText("JohnDoeler")));
        onView(withId(R.id.emailField)).check(matches(withText("Johhny@gmail.com")));



        //




    }
}
