package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.luckyevent.organizer.eventDetails.EventDetailsFragment;
import com.example.luckyevent.organizer.eventDetails.QrDisplayFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventDetailsFragmentTest {

    @Test
    public void testEventQrCodeClickLaunchesQrDisplayFragment() {
        // Launch EventDetailsFragment with the event ID argument
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "test_event_id");

        FragmentScenario<EventDetailsFragment> scenario =
                FragmentScenario.launch(EventDetailsFragment.class, bundle);

        // Click on the QR Code button (assuming it's identified by R.id.qrCodeButton)
        onView(withId(R.id.event_qr_link)).perform(click());

        // Verify if QrDisplayFragment is displayed
        onView(withId(R.id.qrImageView)).check(matches(isDisplayed()));

        // Verify if progress bar or toolbar appears in the QrDisplayFragment
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
        onView(withId(R.id.topBar)).check(matches(isDisplayed()));
    }

    @Test
    public void testQrCodeGenerationAndDisplay() {
        // Launch QrDisplayFragment directly with the event ID argument
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "test_event_id");

        FragmentScenario<QrDisplayFragment> scenario =
                FragmentScenario.launch(QrDisplayFragment.class, bundle);

        // Simulate QR code fetching and verify if the ImageView is displayed
        onView(withId(R.id.qrImageView)).check(matches(isDisplayed()));
    }
}
