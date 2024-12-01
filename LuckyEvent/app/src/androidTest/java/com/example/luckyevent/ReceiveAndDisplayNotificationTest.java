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
 * Tests if a user's notifications are displayed and regularly updated. Uses the following classes:
 * NotificationService and DisplayNotificationsFragment.
 *
 * IMPORTANT: test passes only if emulator is fast enough
 */
public class ReceiveAndDisplayNotificationTest {
    // data used for tests
    private final String username = "testEntrantM";
    private final String password = "entrantusedformmelvestesting";
    private final String userId = "huDuiipQdhbZHDMEmi6UxySXiLq2";

    private String createUniqueString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        Date currentTime = Calendar.getInstance().getTime();
        return sdf.format(currentTime);
    }

    private void startNotificationService(String uniqueString) {
        ArrayList<String> userIdsList = new ArrayList<>();
        userIdsList.add(userId);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NotificationService.class);
        intent.putStringArrayListExtra("entrantIds", userIdsList);
        intent.putExtra("eventId", "1234");
        intent.putExtra("title", "Test");
        intent.putExtra("description", uniqueString);
        ApplicationProvider.getApplicationContext().startService(intent);
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void displayEntrantNotificationsTest() throws InterruptedException {
        // login and navigate to user notifications screen
        onView(withId(R.id.usernameInput)).perform(ViewActions.typeText(username));
        onView(withId(R.id.passwordInput)).perform(ViewActions.typeText(password));
        onView(withId(R.id.SignInButton)).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.notification_item))
                .check(matches(isDisplayed()))
                .perform(click());

        // create a unique string that we will use to check if a new notification is displayed
        String uniqueString = createUniqueString();
        onView(withText(uniqueString)).check(doesNotExist());

        startNotificationService(uniqueString);
        Thread.sleep(5000);

        onView(withText(uniqueString)).check(matches(isDisplayed()));

        // delete notification
        onView(withId(R.id.image_trash)).perform(click());
    }
}
