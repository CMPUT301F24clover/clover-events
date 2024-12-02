package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.luckyevent.shared.LoginActivity;
import com.example.luckyevent.organizer.sendNotification.NotificationService;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Tests if the user can view and receive notifications. Uses the following classes:
 * NotificationService and DisplayNotificationsFragment.
 */
@RunWith(AndroidJUnit4.class)
public class ReceiveAndDisplayNotificationTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Logs into the application.
     */
    private void login() throws InterruptedException {
        String username = "testEntrantM";
        String password = "entrantusedformmelvestesting";

        onView(withId(R.id.username_editText)).perform(ViewActions.typeText(username), hideKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.password_editText)).perform(ViewActions.typeText(password), hideKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.sign_in_button)).perform(click());
        Thread.sleep(5000);
    }

    /**
     * Hides the keyboard immediately after typing.
     */
    private static ViewAction hideKeyboard() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        };
    }

    /**
     * Creates a unique string using the current date and time.
     * @return A string of the current date and time.
     */
    private String createUniqueString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        Date currentTime = Calendar.getInstance().getTime();
        return sdf.format(currentTime);
    }

    /**
     * Starts the notification service.
     * @param uniqueString A string that will be used as the notification's message.
     */
    private void startNotificationService(String uniqueString) {
        String userId = "huDuiipQdhbZHDMEmi6UxySXiLq2";
        ArrayList<String> userIdsList = new ArrayList<>();
        userIdsList.add(userId);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NotificationService.class);
        intent.putStringArrayListExtra("entrantIds", userIdsList);
        intent.putExtra("eventId", "1234");
        intent.putExtra("title", "Test");
        intent.putExtra("description", uniqueString);
        ApplicationProvider.getApplicationContext().startService(intent);
    }

    @Test
    public void displayEntrantNotificationsTest() throws InterruptedException {     
        login();

        // navigate to user notifications screen
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
