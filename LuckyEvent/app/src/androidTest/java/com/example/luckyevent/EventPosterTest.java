package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onData;
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

import static org.hamcrest.Matchers.anything;

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

public class EventPosterTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents before the test
        Intents.init();
    }

    public void navigateToEventPoster() throws InterruptedException{
        // Click on the sign in button and register as an entrant
        onView(withId(R.id.sign_in_as_organizer_button)).perform(click());
        onView(withId(R.id.username_editText)).perform(replaceText("JerryDpe"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());


        // Check if the bottom navigation bar is displayed and click on the events item
        Thread.sleep(5000);
        onView(withId(R.id.bottomNavigationViewOrganizer)).check(matches(isDisplayed()));
        onView(withId(R.id.events_item)).perform(ViewActions.click());

        Thread.sleep(2000);
        // Click on the first event on the list
        onData(anything())
                .inAdapterView(withId(R.id.customListView))
                .atPosition(0)
                .perform(click());

        // Navigate the event poster screen
        Thread.sleep(2000);
        onView(withId(R.id.event_poster_link)).perform(ViewActions.click());
    }

    @Test
    public void testEditPoster() throws InterruptedException {
        navigateToEventPoster();
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("android.resource://android.drawable.ic_menu_camera");
        resultData.setData(imageUri);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub the intent triggered by ACTION_GET_CONTENT inside the CHOOSER intent
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"));
        Intents.intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(result);

        // Click on the button to trigger the intent
        onView(withId(R.id.add_edit_poster_button)).perform(click());

        // Verify that the intent was launched
        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_GET_CONTENT)));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("image/*")));

        Thread.sleep(4000);
        // the test passes if we are still in the event poster page
        onView(withId(R.id.add_edit_poster_button)).check(matches(isDisplayed()));
        Intents.release();
    }

    @Test
    public void testRemovePoster() throws InterruptedException {
        navigateToEventPoster();
        // Click the remove event poster button
        onView(withId(R.id.delete_event_poster_button)).perform(click());

        Thread.sleep(2000);
        // the test passes if we are still in the event poster page
        onView(withId(R.id.add_edit_poster_button)).check(matches(isDisplayed()));
        Intents.release();
    }


}
