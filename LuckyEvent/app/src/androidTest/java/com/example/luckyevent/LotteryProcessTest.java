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

import com.example.luckyevent.activities.LoginActivity;
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
 * Tests the lottery process and if the organizer can interact with their various entrants lists.
 * User stories being tested:
 * viewAndNotifyWaitlistedEntrantsTest - US 02.02.01, US 02.07.01
 * lotteryProcessTest - US 01.04.01, US 01.04.02, US 02.05.01, US 02.05.02, US 02.06.01, US 02.07.02
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LotteryProcessTest {
    private final String TAG = "LotteryProcessTest";
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
        eventName = "Test Lottery" + randomNum;

        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName);
        eventInfo.put("dueDate", "Tonight");
        eventInfo.put("dateAndTime", "Today · Right Now");
        eventInfo.put("description", "This event is for testing purposes only.");
        eventInfo.put("waitListSize", -1);
        eventInfo.put("sampleSize", 1);
        eventInfo.put("currentWaitList", 3);
        eventInfo.put("organizerId", organizerId);
        eventInfo.put("createdAt", System.currentTimeMillis());
        eventInfo.put("geolocationRequired", false);

        db = FirebaseFirestore.getInstance();

        // add the event to the database
        db.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    eventId = documentReference.getId();

                    // add event ID to organizer's list of events
                    db.collection("loginProfile")
                            .document(organizerId)
                            .update("myEvents", FieldValue.arrayUnion(eventId));

                    // add entrants to event's waiting list
                    addToWaitlist(entrantAId);
                    addToWaitlist(entrantBId);
                    addToWaitlist(entrantCId);

                    latch.countDown();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add event", e);
                    latch.countDown();
                });
    }

    /**
     * Adds entrant to event waiting list using a Firestore transaction.
     * Updates the event's waiting list sub-collection and the entrant's events.
     * @param entrantId ID of the entrant joining the waiting list.
     */
    private void addToWaitlist(String entrantId) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // prepare data that will be added to event's waiting list
            Map<String, Object> waitingListEntry = new HashMap<>();
            waitingListEntry.put("userId", entrantId);

            // prepare data that will be added to a user's joined events
            Map<String, Object> eventJoinedEntry = new HashMap<>();
            eventJoinedEntry.put("eventId", eventId);
            eventJoinedEntry.put("status", "Waitlisted");

            // execute transaction to update all documents
            transaction.set(db.collection("events").document(eventId).collection("waitingList").document(entrantId), waitingListEntry);
            transaction.set(db.collection("loginProfile").document(entrantId).collection("eventsJoined").document(eventId), eventJoinedEntry);

            return null;
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to add entrant", e));
    }

    @After
    public void tearDown() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // execute transaction to update all documents
            transaction.delete(db.collection("events").document(eventId));
            transaction.update(db.collection("loginProfile").document(organizerId), "myEvents", FieldValue.arrayRemove(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantAId).collection("eventsJoined").document(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantBId).collection("eventsJoined").document(eventId));
            transaction.delete(db.collection("loginProfile").document(entrantCId).collection("eventsJoined").document(eventId));

            return null;
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to delete event", e));
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

    private void addEntrantToRelevantList(String entrantName, String entrantId, List<String> winnerIds, List<String> loserIds) {
        try {
            onView(withText(entrantName)).check(matches(isDisplayed()));
            winnerIds.add(entrantId);
        } catch (Exception e) {
            loserIds.add(entrantId);
        }
    }

    @Test
    public void viewAndNotifyWaitlistedEntrantsTest() throws InterruptedException {
        // navigate to organizer login page
        onView(withId(R.id.sign_in_as_organizer_button)).perform(click());
        Thread.sleep(2000);
        login();

        // navigate to organizer's events screen
        onView(withId(R.id.events_item))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        // navigate to event screen
        onView(withId(R.id.text_showMore))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // view waiting list
        onView(withId(R.id.waiting_list_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withText("Test Entrant A")).check(matches(isDisplayed()));
        onView(withText("Test Entrant B")).check(matches(isDisplayed()));
        onView(withText("Test Entrant C")).check(matches(isDisplayed()));

        // create and send notification
        String notifDesc = createUniqueString();
        onView(withId(R.id.create_notification_fab)).perform(click());
        onView(withId(R.id.edit_notif_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(ViewActions.typeText("Testing US 02.07.01"), hideKeyboard());
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
        entrantIds.add(entrantBId);
        entrantIds.add(entrantCId);
        checkNotifSent(entrantIds, notifDesc);

        pressBack();
    }

    @Test
    public void lotteryProcessTest() throws InterruptedException {
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

        // view empty list of chosen entrants then go back
        onView(withId(R.id.chosen_entrant_button))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);

        onView(withText("No entrants"))
                .check(matches(isDisplayed()));
        pressBack();
        Thread.sleep(1000);

        // start the lottery
        onView(withId(R.id.sample_entrants))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(10000);

        // view entrant in chosen entrants list
        onView(withId(R.id.chosen_entrant_button))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.button_cancelEntrant))
                .check(matches(isDisplayed()));

        // check if winners and losers were notified of lottery results
        ArrayList<String> winnerIds = new ArrayList<>();
        ArrayList<String> loserIds = new ArrayList<>();
        addEntrantToRelevantList("Test Entrant A", entrantAId, winnerIds, loserIds);
        addEntrantToRelevantList("Test Entrant B", entrantBId, winnerIds, loserIds);
        addEntrantToRelevantList("Test Entrant C", entrantCId, winnerIds, loserIds);
        checkNotifSent(winnerIds, String.format("You have been chosen to sign up for %s! Go to 'My Waiting Lists' to accept your invitation.", eventName));
        checkNotifSent(loserIds, String.format("We regret to inform you that you have not been chosen to sign up for %s. Thank you for your interest.", eventName));

        // create and send notification
        String notifDesc = createUniqueString();
        onView(withId(R.id.create_notification_fab)).perform(click());
        onView(withId(R.id.edit_notif_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(ViewActions.typeText("Testing US 02.07.02"), hideKeyboard());
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
        checkNotifSent(winnerIds, notifDesc);

        pressBack();
        Thread.sleep(5000);
    }
}
