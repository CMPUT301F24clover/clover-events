package com.example.luckyevent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.luckyevent.organizer.OrganizerMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateEventTest {

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Before
    public void setUp() {
        // Setup Firebase and authentication mocks if needed
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Test
    public void testCreateEvent() {
        // Launch the Activity that contains the fragment (or launch the fragment directly)
        ActivityScenario.launch(OrganizerMenuActivity.class);

        // Wait for the fragment to load
        onView(withId(R.id.topBar)).check(matches(ViewMatchers.isDisplayed()));

        // Interact with the UI components to fill out the form

        // Fill in event name
        onView(withId(R.id.input_eventName)).perform(typeText("Sample Event"));

        // Select due date
        onView(withId(R.id.input_due_date)).perform(click());
        // Assume the date picker is set up for a date in the future
        onView(withId(android.R.id.button1)).perform(click());  // Confirm the date

        // Select sample size dropdown
        onView(withId(R.id.sampleSizeDropdown)).perform(click());
        onView(withText("50")).perform(click()); // Select 50

        // Select waitlist size dropdown
        onView(withId(R.id.waitingListSizeDropdown)).perform(click());
        onView(withText("40")).perform(click()); // Select 40

        // Fill in description
        onView(withId(R.id.input_description)).perform(typeText("This is a sample event"));

        // Set geolocation checkbox
        onView(withId(R.id.geolocation_checkbox)).perform(click());

        // Click the "Create Event" button
        onView(withId(R.id.button_createEvent)).perform(click());

        // Verify Toast message (assuming Toast is shown after successful creation)
        onView(withText("Event Created Successfully!")).inRoot(new ToastMatcher())  // Custom ToastMatcher class
                .check(matches(ViewMatchers.isDisplayed()));

        // Optionally, check if navigation to event details occurred
        // onView(withId(R.id.some_other_view_in_details)).check(matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void tearDown() {
        // Cleanup Firebase instance if needed or other resources
    }

}
