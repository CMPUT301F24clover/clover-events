package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Tests the following classes: NotificationService and DisplayNotificationsFragment.
 */
public class ReceiveAndDisplayNotificationTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void displayEntrantNotificationsTest() throws InterruptedException {
        // login and navigate to user notifications screen
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText("JohnDoe"));
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
        onView(withId(R.id.SignInButton)).perform(click());

        Thread.sleep(5000);
        onView(withId(R.id.notification_item))
                .check(matches(isDisplayed()))
                .perform(click());

        // create a unique string to send to the user in a notification
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        Date currentTime = Calendar.getInstance().getTime();
        String uniqueText = sdf.format(currentTime);

        onView(withText(uniqueText)).check(doesNotExist());
        Thread.sleep(5000);

        // start NotificationService to send notification to the user
        ArrayList<String> userIdsList = new ArrayList<>();
        userIdsList.add("Bhs47JTuD3gwwlefhM8DnMrkTQT2");
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NotificationService.class);
        intent.putStringArrayListExtra("entrants", userIdsList);
        intent.putExtra("title", "Test");
        intent.putExtra("description", uniqueText);
        ApplicationProvider.getApplicationContext().startService(intent);

        Thread.sleep(5000);
        onView(withText(uniqueText)).check(matches(isDisplayed()));
    }
}
