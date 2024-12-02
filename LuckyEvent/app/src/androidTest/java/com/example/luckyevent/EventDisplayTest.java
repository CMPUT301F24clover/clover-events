package com.example.luckyevent;

import androidx.fragment.app.FragmentTransaction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.luckyevent.organizer.OrganizerMenuActivity;
import com.example.luckyevent.organizer.eventDetails.EventDetailsFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.mockito.Mockito.*;

import android.os.Bundle;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EventDisplayTest {

    @Rule
    public ActivityScenarioRule<OrganizerMenuActivity> activityScenarioRule = new ActivityScenarioRule<>(OrganizerMenuActivity.class);

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Before
    public void setUp() throws Exception {
        firestore = mock(FirebaseFirestore.class);
        firebaseAuth = mock(FirebaseAuth.class);

        // Mock the current user and their UID
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(firebaseAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        // Use dependency injection or setup mocks for Firestore here
        // For instance, you can mock Firestore operations.
    }

    @Test
    public void testDisplayEventDetails() {
        // Create a sample event document for Firestore
        DocumentSnapshot eventDoc = mock(DocumentSnapshot.class);
        when(eventDoc.exists()).thenReturn(true);
        when(eventDoc.getString("eventName")).thenReturn("Sample Event");
        when(eventDoc.getString("description")).thenReturn("This is a test event description.");
        when(eventDoc.getString("dateAndTime")).thenReturn("2024-12-12 14:00");
        when(eventDoc.getLong("currentWaitList")).thenReturn(10L);

        // Simulate the Firestore document retrieval
        when(firestore.collection("events").document("sampleEventId").get()).thenReturn(mockTask(eventDoc));

        // Launch the fragment using the ActivityScenario
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "sampleEventId");
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(bundle);

        // Wait for the fragment to load (Avoid Thread.sleep, use IdlingResources for real tests)
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));

        // Check if event details are correctly displayed
        onView(withId(R.id.textView_eventTitle2)).check(matches(withText("Sample Event")));
        onView(withId(R.id.textView_description2)).check(matches(withText("This is a test event description.")));
        onView(withId(R.id.textView_dateTime2)).check(matches(withText("2024-12-12 14:00")));
        onView(withId(R.id.textView_capacity2)).check(matches(withText("Entrants in Waiting List: 10")));
    }

    @Test
    public void testHandleEmptyEventDetails() {
        // Simulate Firestore document not found
        DocumentSnapshot eventDoc = mock(DocumentSnapshot.class);
        when(eventDoc.exists()).thenReturn(false);

        // Simulate the Firestore document retrieval failure
        when(firestore.collection("events").document("invalidEventId").get()).thenReturn(mockTask(eventDoc));

        // Launch the fragment with an invalid eventId
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "invalidEventId");
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(bundle);

        // Wait for the fragment to load
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));

        // Verify if a toast message or UI error is shown (you can check for specific Toast if needed)
        onView(withText("Event not found")).check(matches(isDisplayed()));
    }

    @Test
    public void testLotteryButton() {
        // Set up mock data for event ID and waiting list
        when(firestore.collection("events").document("sampleEventId").collection("chosenEntrants").get()).thenReturn(mockEmptyQuerySnapshot());

        // Launch the fragment
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "sampleEventId");
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(bundle);

        // Wait for the fragment to load
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));

        // Perform a click on the "sample entrants" button and verify the lottery process starts
        onView(withId(R.id.sample_entrants)).perform(click());

        // Verify that the lottery process starts by checking the visibility of related UI elements
        // E.g., if a toast or message is displayed, you can validate that.
        onView(withText("Conducting lottery...")).check(matches(isDisplayed()));
    }

    // Utility method to mock Firestore Task with a document snapshot
    private Task<DocumentSnapshot> mockTask(DocumentSnapshot document) {
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(document);
        return mockTask;
    }

    // Utility method to mock an empty Firestore QuerySnapshot
    private Task<QuerySnapshot> mockEmptyQuerySnapshot() {
        Task<QuerySnapshot> mockTask = mock(Task.class);
        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.isEmpty()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        return mockTask;
    }
}

