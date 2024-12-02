package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AdminBrowseEventsTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    // first login to an admin account then navigate to the browse event page
    public void navigationToBrowseEvent(){
        // fill out the sign in fields
        onView(withId(R.id.username_editText)).perform(typeText("AdminTest"));
        onView(withId(R.id.password_editText)).perform(typeText("test123"));
        // click sign in and pause for 5 seconds to allow everything to load (without pausing it goes to fast)
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check to see if we see the admin bottom navigation bar meaning we were able to sign in as an admin
        onView(withId(R.id.bottomNavigationViewAdmin)).check(matches(isDisplayed()));
        // click the event icon and go to the browse events page
        onView(withId(R.id.event_item)).perform(ViewActions.click());
        // if we see the search button that means we were able to navigate to the browse events page
        onView(withId(R.id.search_event_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseEvents(){
        navigationToBrowseEvent();

        // search for an item and hit search
        onView(withId(R.id.searchEditText)).perform(typeText("Testing Browse Events"));
        onView(withId(R.id.search_event_button)).perform(ViewActions.click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // make sure the right event gets displayed
        onView(withId(R.id.eventName)).check(matches(withText("Testing Browse Events (testcase)")));

    }

    @Test
    public void testDeleteQrAndEvent(){
        navigationToBrowseEvent();

        List<String> start = Arrays.asList("AAA","AAB","AAC","AAD","AAE");

        // search for an item and hit search
        String randomStart = start.get(new Random().nextInt(start.size()));
        String search = randomStart + " admin browse events";
        onView(withId(R.id.searchEditText)).perform(ViewActions.clearText(),typeText(search));
        onView(withId(R.id.search_event_button)).perform(ViewActions.click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // check if qr code exist
        onView(withId(R.id.eventQr)).check(matches(not(withText("No Qr code for this event"))));

        //click delete qr
        onView(withId(R.id.eventRemoveQrData)).perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // now check if the qr is removed after refreshing (click search again)
        onView(withId(R.id.search_event_button)).perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onView(withId(R.id.eventQr)).check(matches(withText("No Qr code for this event")));

        // now delete the event
        onView(withId(R.id.eventRemoveButton)).perform(ViewActions.click());
    }
}
