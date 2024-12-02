package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.luckyevent.activities.OrganizerMenuActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityManagementTest {

    @Rule
    public ActivityTestRule<OrganizerMenuActivity> activityRule =
            new ActivityTestRule<>(OrganizerMenuActivity.class);

    private final String FACILITY_NAME = "Test Facility";
    private final String FACILITY_EMAIL = "test@facility.com";
    private final String FACILITY_ADDRESS = "123 Test St, Test City";
    private final String FACILITY_PHONE = "1234567890";

    private final String UPDATED_FACILITY_NAME = "Updated Facility";
    private final String UPDATED_FACILITY_EMAIL = "updated@facility.com";
    private final String UPDATED_FACILITY_ADDRESS = "456 Updated St, Updated City";
    private final String UPDATED_FACILITY_PHONE = "0987654321";

    /**
     * Test creating a new facility, displaying it, and editing the details.
     */
    @Test
    public void testCreateDisplayEditFacility() {
        // Navigate to CreateFacilityFragment and fill in the details
        onView(withId(R.id.button_createFacility)).perform(click());

        onView(withId(R.id.input_facilityName)).perform(replaceText(FACILITY_NAME));
        onView(withId(R.id.input_facilityEmail)).perform(replaceText(FACILITY_EMAIL));
        onView(withId(R.id.input_facilityAddress)).perform(replaceText(FACILITY_ADDRESS));
        onView(withId(R.id.input_facilityPhone)).perform(replaceText(FACILITY_PHONE));

        // Click the Create Facility button
        onView(withId(R.id.button_createFacility)).perform(click());

        // Verify facility details are displayed correctly
        onView(withId(R.id.facility_nameField)).check(matches(withText(FACILITY_NAME)));
        onView(withId(R.id.facility_emailField)).check(matches(withText(FACILITY_EMAIL)));
        onView(withId(R.id.facility_addressField)).check(matches(withText(FACILITY_ADDRESS)));
        onView(withId(R.id.facility_phoneField)).check(matches(withText(FACILITY_PHONE)));

        // Navigate to EditFacilityFragment
        onView(withId(R.id.editFacilityButton)).perform(click());

        // Update the facility details
        onView(withId(R.id.input_editFacilityName)).perform(clearText(), replaceText(UPDATED_FACILITY_NAME));
        onView(withId(R.id.input_editFacilityEmail)).perform(clearText(), replaceText(UPDATED_FACILITY_EMAIL));
        onView(withId(R.id.input_editFacilityAddress)).perform(clearText(), replaceText(UPDATED_FACILITY_ADDRESS));
        onView(withId(R.id.input_editFacilityPhone)).perform(clearText(), replaceText(UPDATED_FACILITY_PHONE));

        // Save the changes
        onView(withId(R.id.saveFacilityChanges)).perform(click());

        // Verify the updated details are displayed correctly
        onView(withId(R.id.facility_nameField)).check(matches(withText(UPDATED_FACILITY_NAME)));
        onView(withId(R.id.facility_emailField)).check(matches(withText(UPDATED_FACILITY_EMAIL)));
        onView(withId(R.id.facility_addressField)).check(matches(withText(UPDATED_FACILITY_ADDRESS)));
        onView(withId(R.id.facility_phoneField)).check(matches(withText(UPDATED_FACILITY_PHONE)));
    }
}
