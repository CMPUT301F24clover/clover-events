package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.luckyevent.shared.LoginActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Tests if the user can accept or decline the invitation to sign up for an event.
 * User stories being tested:
 * acceptInvitationTest - US 01.05.02, US 02.06.03
 * declineInvitationTest - US 01.05.03
 */
@RunWith(AndroidJUnit4.class)
public class AcceptDeclineInvitationTest {
    private final String TAG = "AcceptDeclineInvitationTest";
    private final String organizerId = "VNIdMpz3pyXAgNZ9F3tnSzW52B03";
    private String eventId;
    private String eventName;
    private final String entrantNId = "iXnqjpcQkYc7YlfWoOxLtZJ6Hmu2";
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Sets up the database so that the tested classes run properly.
     */
    @Before
    public void setUp() {
        CountDownLatch latch = new CountDownLatch(1);
        Random random = new Random();
        int randomNum = random.nextInt(100);
        eventName = "Test Invite" + randomNum;

        // prepare event data for database
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName);
        eventInfo.put("dueDate", "Tonight");
        eventInfo.put("dateAndTime", "Today · Right Now");
        eventInfo.put("description", "This event is for testing purposes only.");
        eventInfo.put("waitListSize", -1);
        eventInfo.put("sampleSize", 1);
        eventInfo.put("currentWaitList", 0);
        eventInfo.put("organizerId", organizerId);
        eventInfo.put("createdAt", System.currentTimeMillis());
        eventInfo.put("geolocationRequired", false);

        db = FirebaseFirestore.getInstance();

        // add event to the database
        db.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    eventId = documentReference.getId();

                    // add event ID to organizer's list of events
                    db.collection("loginProfile")
                            .document(organizerId)
                            .update("myEvents", FieldValue.arrayUnion(eventId));

                    // add entrant to event's chosen entrants list
                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        // prepare data that will be added to event's chosen entrants list
                        Map<String, Object> chosenEntrantEntry = new HashMap<>();
                        chosenEntrantEntry.put("userId", entrantNId);
                        chosenEntrantEntry.put("invitationStatus", "Pending");

                        // prepare data that will be added to a user's joined events
                        Map<String, Object> eventJoinedEntry = new HashMap<>();
                        eventJoinedEntry.put("eventId", eventId);
                        eventJoinedEntry.put("status", "Chosen");

                        // execute transaction to update all documents
                        transaction.set(db.collection("events").document(eventId).collection("chosenEntrants").document(entrantNId), chosenEntrantEntry);
                        transaction.set(db.collection("loginProfile").document(entrantNId).collection("eventsJoined").document(eventId), eventJoinedEntry);

                        return null;
                    }).addOnFailureListener(e -> Log.e(TAG, "Failed to add entrant", e));

                    latch.countDown();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add event", e);
                    latch.countDown();
                });
    }

    @After
    public void tearDown() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // delete documents created for test
            transaction.delete(db.collection("events").document(eventId));
            transaction.delete(db.collection("events").document(eventId).collection("chosenEntrants").document(entrantNId));
            transaction.update(db.collection("loginProfile").document(organizerId), "myEvents", FieldValue.arrayRemove(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantNId).collection("eventsJoined").document(eventId));

            return null;
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to delete event", e));
    }

    /**
     * Logs into the application.
     */
    private void login(String username, String password) throws InterruptedException {
        onView(withId(R.id.username_editText)).perform(ViewActions.typeText(username), hideKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.password_editText)).perform(ViewActions.typeText(password), hideKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.sign_in_button)).perform(click());
        Thread.sleep(5000);
    }

    /**
     * Hides the keyboard immediately after typing.
     */
    private static ViewAction hideKeyboard() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        };
    }

    @Test
    public void acceptInvitationTest() throws InterruptedException {
        login("testEntrantN", "entrantusedformmelvestesting");

        // navigate to user's waiting lists screen
        onView(withId(R.id.waiting_list_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        // navigate to event screen
        onView(withText(eventName))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // accept invitation and check if screen reflects the user's decision
        onView(withId(R.id.button_acceptInvitation))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        onView(withText("Yes"))
                .inRoot(isDialog())
                .perform(ViewActions.click());
        Thread.sleep(2000);

        onView(withText("Enrolled"))
                .check(matches(isDisplayed()));
        pressBack();

        onView(withId(R.id.profile_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.button3))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // log in to organizer account to view chosen entrants list
        onView(withId(R.id.sign_in_as_organizer_button)).perform(click());
        Thread.sleep(2000);
        login("testOrganizerM", "organizerusedformmelvestesting");

        onView(withId(R.id.events_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withText(eventName))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.enrolled_entrant_button))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withText("Test Entrant N")).check(matches(isDisplayed()));
    }

    @Test
    public void declineInvitationTest() throws InterruptedException {
        login("testEntrantN", "entrantusedformmelvestesting");

        // navigate to user's waiting lists screen
        onView(withId(R.id.waiting_list_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        // navigate to event screen
        onView(withText(eventName))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // accept invitation and check if screen reflects the user's decision
        onView(withId(R.id.button_declineInvitation))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        onView(withText("Yes"))
                .inRoot(isDialog())
                .perform(ViewActions.click());
        Thread.sleep(2000);

        onView(withText("Declined invitation"))
                .check(matches(isDisplayed()));
    }
}
