package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
import androidx.test.filters.LargeTest;

import com.example.luckyevent.shared.LoginActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Tests if the organizer can cancel an entrant and replace them by sampling a new one.
 * User stories being tested: US 02.05.03 (and US 01.05.01 by extension), US 02.06.02, US 02.06.04,
 * US 02.07.03
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SampleReplacementEntrantTest {
    private final String TAG = "SampleReplacementEntrantTest";
    private final String organizerId = "VNIdMpz3pyXAgNZ9F3tnSzW52B03";
    private String eventId;
    private String eventName;
    private final String entrantAId = "gvfqStzQlhVRhOeRJLFgM95AM2G3";
    private final String entrantBId = "5OVXycHVpPQRY2r7zmdiLKuCMVf1";
    private final String entrantCId = "Iam3XmnLi7bqb6d1XPewxaQvlqE3";
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
        eventName = "Test Sample Replacement" + randomNum;

        // prepare event data for database
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName);
        eventInfo.put("dueDate", "Tonight");
        eventInfo.put("dateAndTime", "Today · Right Now");
        eventInfo.put("description", "This event is for testing purposes only.");
        eventInfo.put("waitListSize", -1);
        eventInfo.put("sampleSize", 1);
        eventInfo.put("currentWaitList", 1);
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
                        // prepare data that will be added to event's chosen entrants sub-collection
                        Map<String, Object> chosenEntrantEntry = new HashMap<>();
                        chosenEntrantEntry.put("userId", entrantAId);
                        chosenEntrantEntry.put("invitationStatus", "Pending");

                        // prepare data that will be added to Entrant A's joined events
                        Map<String, Object> eventJoinedEntryChosen = new HashMap<>();
                        eventJoinedEntryChosen.put("eventId", eventId);
                        eventJoinedEntryChosen.put("status", "Chosen");

                        // prepare data that will be added to event's waiting list
                        Map<String, Object> waitingListEntry = new HashMap<>();
                        waitingListEntry.put("userId", entrantBId);

                        // prepare data that will be added to Entrant B's joined events
                        Map<String, Object> eventJoinedEntryWaitlisted = new HashMap<>();
                        eventJoinedEntryWaitlisted.put("eventId", eventId);
                        eventJoinedEntryWaitlisted.put("status", "Waitlisted");

                        // execute transaction to update all documents
                        transaction.set(db.collection("events").document(eventId).collection("chosenEntrants").document(entrantAId), chosenEntrantEntry);
                        transaction.set(db.collection("loginProfile").document(entrantAId).collection("eventsJoined").document(eventId), eventJoinedEntryChosen);
                        transaction.set(db.collection("events").document(eventId).collection("waitingList").document(entrantBId), waitingListEntry);
                        transaction.set(db.collection("loginProfile").document(entrantBId).collection("eventsJoined").document(eventId), eventJoinedEntryWaitlisted);

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
            transaction.delete(db.collection("events").document(eventId).collection("chosenEntrants").document(entrantAId));
            transaction.delete(db.collection("events").document(eventId).collection("chosenEntrants").document(entrantBId));
            transaction.delete(db.collection("events").document(eventId));
            transaction.update(db.collection("loginProfile").document(organizerId), "myEvents", FieldValue.arrayRemove(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantAId).collection("eventsJoined").document(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantBId).collection("eventsJoined").document(eventId));

            return null;
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to delete event", e));
    }

    /**
     * Logs into the application.
     */
    private void login() throws InterruptedException {
        String username = "testOrganizerM";
        String password = "organizerusedformmelvestesting";

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
    public static ViewAction hideKeyboard() {
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

    /**
     * Creates a unique string using the current date and time.
     * @return A string of the current date and time.
     */
    private String createUniqueString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        Date currentTime = Calendar.getInstance().getTime();
        return sdf.format(currentTime);
    }

    /**
     * Checks if the entrants received the notification sent by the organizer.
     * @param entrantIds IDs of entrants that are supposed to receive the notification.
     * @param desc A unique string used to test if the notification is present in the entrants'
     *             notifications sub-collection.
     */
    private void checkNotifSent(List<String> entrantIds, String desc) {
        for (String entrantId : entrantIds) {
            db.collection("loginProfile")
                    .document(entrantId)
                    .collection("notifications")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean notifSent = false;

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            // Check if the document contains the expected content
                            if (doc.getString("content").equals(desc)) {
                                notifSent = true;
                                break;
                            }
                        }
                        Assert.assertTrue("Notification was not sent to entrant " + entrantId, notifSent);
                    }).addOnFailureListener(e -> Log.w(TAG, "Error getting documents", e));
        }
    }

    @Test
    public void sampleReplacementEntrantTest() throws InterruptedException {
        // navigate to organizer login page
        onView(withId(R.id.sign_in_as_organizer_button)).perform(click());
        Thread.sleep(2000);
        login();

        // navigate to organizer's events screen
        onView(withId(R.id.events_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        // navigate to the newly created event screen
        onView(withText(eventName))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // view empty list of cancelled entrants then go back
        onView(withId(R.id.cancelled_entrant_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

        onView(withText("No entrants")).check(matches(isDisplayed()));
        pressBack();
        Thread.sleep(1000);

        // navigate to list of chosen entrants and cancel entrant
        onView(withId(R.id.chosen_entrant_button))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.button_cancelEntrant))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        // sample new entrant
        onView(withId(R.id.button_sampleNewEntrant))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withText("Test Entrant B")).check(matches(isDisplayed()));
        pressBack();
        Thread.sleep(1000);

        // view entrant in cancelled entrants list
        onView(withId(R.id.cancelled_entrant_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withText("Test Entrant A")).check(matches(isDisplayed()));

        // create and send notification
        String notifDesc = createUniqueString();
        onView(withId(R.id.create_notification_fab)).perform(click());
        onView(withId(R.id.edit_notif_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(ViewActions.typeText("Testing US 02.07.03"), hideKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.edit_notif_desc))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(ViewActions.typeText(notifDesc), hideKeyboard());
        Thread.sleep(1000);
        onView(withText("Confirm"))
                .inRoot(isDialog())
                .perform(click());

        // check if notification was sent
        ArrayList<String> entrantIds = new ArrayList<>();
        entrantIds.add(entrantAId);
        checkNotifSent(entrantIds, notifDesc);

        pressBack();
    }
}
