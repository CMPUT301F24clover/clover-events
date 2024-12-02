package com.example.luckyevent;

import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.luckyevent.organizer.eventCreation.CreateEventFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class CreateEventFragmentTest {

    private static final String EVENT_NAME = "Test Event";
    private static final String EVENT_DESCRIPTION = "This is a test description.";
    private static final String EVENT_DATE = "December 25, 2024"; // Example date format
    private static final String EVENT_TIME = "2:00 PM - 4:00 PM";

    @Before
    public void setUp() {
        // Launch CreateEventFragment using FragmentScenario
        FragmentScenario.launchInContainer(CreateEventFragment.class, new Bundle(), R.style.Base_Theme_LuckyEvent);
    }

    @Test
    public void testCreateEventSuccess() {
        // Enter event name
        onView(withId(R.id.input_eventName))  // Adjust for correct ID for EditText in TextInputLayout
                .perform(typeText(EVENT_NAME), closeSoftKeyboard());

        // Enter due date
        onView(withId(R.id.input_due_date))  // Adjust for correct ID for EditText in TextInputLayout
                .perform(click());
        // Simulate selecting a date from DatePickerDialog
        onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2024, 12, 25));
        onView(withText("OK")).perform(click());

        // Enter event time
        onView(withId(R.id.input_date_and_time))  // Adjust for correct ID for EditText in TextInputLayout
                .perform(click());
        // Simulate selecting time "From" and "To" for TimePicker
        onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(14, 0));
        onView(withText("OK")).perform(click());
        onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(16, 0));
        onView(withText("OK")).perform(click());

        // Enter event description
        onView(withId(R.id.input_description))
                .perform(typeText(EVENT_DESCRIPTION), closeSoftKeyboard());

        // Select a sample size
        onView(withId(R.id.sampleSizeDropdown))
                .perform(click());
        onView(withText("50")).perform(click());

        // Click Create Event Button
        onView(withId(R.id.button_createEvent))
                .perform(click());

        // Verify event creation
        onView(withText("Event Created Successfully!"))
                .inRoot(ToastMatcher.isToast())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCreateEventValidationError() {
        // Leave all fields empty and click Create Event button
        onView(withId(R.id.button_createEvent)).perform(click());

        // Verify validation error message
        onView(withText("Please fill in all fields"))
                .inRoot(ToastMatcher.isToast())
                .check(matches(isDisplayed()));
    }
}
