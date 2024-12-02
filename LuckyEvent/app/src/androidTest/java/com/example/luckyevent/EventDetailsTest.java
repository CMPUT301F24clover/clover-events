package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.luckyevent.organizer.OrganizerMenuActivity;
import com.example.luckyevent.organizer.eventDetails.EventDetailsFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for EventDetailsFragment to verify that event details are displayed properly.
 */
@RunWith(AndroidJUnit4.class)
public class EventDetailsTest {

    private static final String MOCK_EVENT_ID = "mockEventId";

    @Rule
    public ActivityTestRule<OrganizerMenuActivity> activityRule = new ActivityTestRule<>(OrganizerMenuActivity.class);

    @Before
    public void setUp() {
        mockFirestoreData();
    }

    @Test
    public void testEventDetailsDisplayedCorrectly() {
        // Launch EventDetailsFragment with a mock eventId
        Bundle bundle = new Bundle();
        bundle.putString("eventId", MOCK_EVENT_ID);
        FragmentScenario.launchInContainer(EventDetailsFragment.class, bundle, R.style.Base_Theme_LuckyEvent);

        // Check if the UI components display the correct data
        onView(withId(R.id.textView_eventTitle2))
                .check(matches(withText("Mock Event Title")));
        onView(withId(R.id.textView_description2))
                .check(matches(withText("Mock Description")));
        onView(withId(R.id.textView_dateTime2))
                .check(matches(withText("November 12, 2024 • 10:00 AM - 5:00 PM")));
        onView(withId(R.id.textView_capacity2))
                .check(matches(withText("Entrants in Waiting List: 25")));
        onView(withId(R.id.textView_location2))
                .check(matches(withText("123 Mock Street, Mock City, MC 12345")));
    }

    /**
     * Mocks Firestore data to test the fragment in isolation.
     */
    private void mockFirestoreData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Mock Event Document
        Map<String, Object> mockEventData = new HashMap<>();
        mockEventData.put("eventName", "Mock Event Title");
        mockEventData.put("description", "Mock Description");
        mockEventData.put("dateAndTime", "November 12, 2024 • 10:00 AM - 5:00 PM");
        mockEventData.put("dueDate","December 11, 2024");
        mockEventData.put("currentWaitList", 25L);

        firestore.collection("events")
                .document(MOCK_EVENT_ID)
                .set(mockEventData);

        // Mock Facility Document
        Map<String, Object> mockFacilityData = new HashMap<>();
        mockFacilityData.put("Address", "123 Mock Street, Mock City, MC 12345");

        firestore.collection("facilities")
                .document("mockFacilityId")
                .set(mockFacilityData);
    }
}
