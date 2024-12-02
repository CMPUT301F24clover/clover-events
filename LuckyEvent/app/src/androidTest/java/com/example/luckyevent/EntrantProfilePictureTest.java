package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class EntrantProfilePictureTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents before the test
        Intents.init();

    }

    public void navigateToProfile() throws InterruptedException{
        // Enter a valid username and password and click on the button to sign in
        onView(withId(R.id.username_editText)).perform(replaceText("JohnDoe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());

        Thread.sleep(5000);
        // Check if the bottom navigation bar is displayed and click on the profile item
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());

        // Wait for the page to load, then click on the edit button
        Thread.sleep(5000);
        onView(withId(R.id.editButton)).perform(ViewActions.click());

        // Click on the edit profile picture button
        onView(withId(R.id.editProfilePictureButton)).perform(ViewActions.click());

    }

    public void selectImage(){
        // Insert our test image into the activity result
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("android.resource://android.drawable.ic_menu_camera");
        resultData.setData(imageUri);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub the intent triggered by ACTION_GET_CONTENT inside the CHOOSER intent
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"));
        Intents.intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(result);
    }

    @Test
    public void testSignUpProfilePictureAddition() throws InterruptedException{
        Random rand = new Random();
        int generatedNumber = rand.nextInt(1000);

        // Click on the sign up button and check if we have successfully navigated to the sign up page
        onView(withId(R.id.sign_up_button)).perform(ViewActions.click());
        onView(withId(R.id.welcome_textView)).check(matches(isDisplayed()));

        // Fill in the fields needed to sign up
        onView(withId(R.id.username_editText)).perform(replaceText("DummyUsers" + Integer.toString(generatedNumber)), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover1"), closeSoftKeyboard());
        onView(withId(R.id.firstname_editText)).perform(replaceText("Dummy"), closeSoftKeyboard());
        onView(withId(R.id.lastname_editText)).perform(replaceText("User"), closeSoftKeyboard());

        //select the image and click on the add image button
        selectImage();
        onView(withId(R.id.button_add_image)).perform(click());

        // Verify that the intent was launched
        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_GET_CONTENT)));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("image/*")));
        Thread.sleep(5000);

        // The button isn't clicking for some reason
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());

        // The test passes if we have navigated to the homepage
        Thread.sleep(5000);
        onView(withId(R.id.mainContentCard)).check(matches(isDisplayed()));
        Intents.release();
    }

    @Test
    public void testEditProfilePicture() throws InterruptedException{
        navigateToProfile();

        selectImage();

        onView(withId(R.id.change_profile_button)).perform(click());

        // Verify that the intent was launched
        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_GET_CONTENT)));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("image/*")));
        Thread.sleep(2000);

        // The test passes if we are still in the edit profile pictures page
        onView(withId(R.id.change_profile_button)).check(matches(isDisplayed()));

        Intents.release();


    }

    @Test
    public void testRemoveProfilePicture() throws InterruptedException{
        navigateToProfile();

        // Click on the remove profile picture button
        onView(withId(R.id.remove_profile_button)).perform(click());

        // The test passes if we are still in the edit profile pictures page
        onView(withId(R.id.change_profile_button)).check(matches(isDisplayed()));

        Intents.release();

    }
}
