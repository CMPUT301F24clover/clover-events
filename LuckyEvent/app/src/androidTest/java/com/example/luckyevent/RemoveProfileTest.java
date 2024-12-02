package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

import android.view.KeyEvent;
import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.shared.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

public class RemoveProfileTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new ActivityScenarioRule<>(LoginActivity.class);

    public void navigateToAdminSection(){
        onView(withId(R.id.username_editText)).perform(replaceText("AdminClover"), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText("clover301"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
    }

    @Test
    public void testRemoveProfile() throws InterruptedException{
        navigateToAdminSection();
        Thread.sleep(5000);

        // Check if we have successfully navigated to the homepage of the admin section
        onView(withId(R.id.bottomNavigationViewAdmin)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        // After pressing the profile item, we check to see if we navigated to the view profiles screen for admins
        onView(withId(R.id.appCompatTextView2)).check(matches(isDisplayed()));


        Thread.sleep(5000);

        // Click on the remove profile button for one of the elements
        onData(anything())
                .inAdapterView(withId(R.id.profile_listview))
                .atPosition(0)
                .onChildView(withId(R.id.remove_profile_button))
                .perform(click());

        // The test passes if we are still on this screen
        onView(withId(R.id.appCompatTextView2)).check(matches(isDisplayed()));
    }

    @Test
    public void browseProfiles() throws InterruptedException{
        navigateToAdminSection();
        Thread.sleep(5000);

        // Check if we have successfully navigated to the homepage of the admin section
        onView(withId(R.id.bottomNavigationViewAdmin)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        // After pressing the profile item, we check to see if we navigated to the view profiles screen for admins
        onView(withId(R.id.appCompatTextView2)).check(matches(isDisplayed()));

        Thread.sleep(5000);

        // Click on the search view to bring down the keyboard
        onView(withId(R.id.searchView))
                .perform(click());

        // Input the username "MerryDoe" into the search field
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(clearText(), typeText("MerryDoe"), closeSoftKeyboard())
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));

        // Check if the result contains an element with the username "MerryDoe"
        onData(anything())
                .inAdapterView(withId(R.id.profile_listview))
                .atPosition(0)
                .onChildView(withId(R.id.userName_textView))
                .check((view, noViewFoundException) -> {
                    if (noViewFoundException != null) {
                        throw noViewFoundException;
                    }
                    TextView textView = (TextView) view;
                    String actualText = textView.getText().toString();
                    assertEquals("MerryDoe", actualText);
                });

        // Click on the remove profile button for this element
        onData(anything())
                .inAdapterView(withId(R.id.profile_listview))
                .atPosition(0)
                .onChildView(withId(R.id.remove_profile_button))
                .perform(click());

        // The test passes if we are still on this screen
        onView(withId(R.id.appCompatTextView2)).check(matches(isDisplayed()));
    }
}
