package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

/**
 * Tests the user's ability to create and edit their profile.
 * User stories being tested: US 01.02.01, US 01.02.02
 */
public class EditProfileTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    public void signupToRegisterProfile(String userName, String pass, String firstName, String lastName){
        // click the signup button
        onView(withId(R.id.sign_up_button)).perform(ViewActions.click());
        // find the SignUpUsernameInput meaning where in the sign up page
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));
        // sign up and input signup fields
        onView(withId(R.id.username_editText)).perform(replaceText(userName), closeSoftKeyboard());
        onView(withId(R.id.password_editText)).perform(replaceText(pass), closeSoftKeyboard());
        onView(withId(R.id.firstname_editText)).perform(replaceText(firstName), closeSoftKeyboard());
        onView(withId(R.id.lastname_editText)).perform(replaceText(lastName), closeSoftKeyboard());

        //click signup
        onView(withId(R.id.create_account_button)).perform(ViewActions.click());
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()));
    }

    public String[] RegisterProfile(){
        // generate random signup/ register fields
        Random random = new Random();
        int randomNum = random.nextInt(100);
        String userName = "pandacool" + randomNum;
        String firstName = "panda" + randomNum;
        String lastName = "bear" + randomNum;
        String name = firstName + " " + lastName;
        String pass = "test123";
        signupToRegisterProfile(userName,pass,firstName,lastName);
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // register the profile with the given random name and the unique gmail
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.typeText(name));
        onView(withId(R.id.emailEditText)).perform(replaceText(userName + "@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.RegisterButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //once we register were brought to the view page, click edit button to go to the edit profile page
        onView(withId(R.id.editButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // check to see if we see the save button meaning were in the edit profile page
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        return new String[]{userName,pass,firstName,lastName};

    }
    @Test
    public void testEditProfileNew(){
        //register a random profile
        String[] loginInfo = RegisterProfile();
        String email = loginInfo[0] + "@gmail.com";
        String name = loginInfo[2] + " " + loginInfo[3];

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int randomNum = random.nextInt(100);
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        // enter the edit fields and click save
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.clearText(),ViewActions.typeText("bob billy"));
        onView(withId(R.id.emailEditText)).perform(ViewActions.click(),ViewActions.clearText(), ViewActions.typeText("testing" + randomNum+ "@gmail.com"));
        onView(withId(R.id.phoneEditText)).perform(replaceText("780-567-0000"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // make sure the fields match the ones we just wrote in the edit profile page
        onView(withId(R.id.nameField)).check(matches(withText("bob billy")));
        onView(withId(R.id.emailField)).check(matches(withText("testing" + randomNum+ "@gmail.com")));
        onView(withId(R.id.phoneField)).check(matches(withText("780-567-0000")));
    }
    @Test
    public void testEditProfileEmptyNameField(){
        //register a random profile
        String[] loginInfo = RegisterProfile();
        String email = loginInfo[0] + "@gmail.com";
        String name = loginInfo[2] + " " + loginInfo[3];

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int randomNum = random.nextInt(100);
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        // leave the name field empty and click save
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.clearText(),ViewActions.typeText(""));
        onView(withId(R.id.emailEditText)).perform(ViewActions.click(),ViewActions.clearText(), ViewActions.typeText("testing" + randomNum+ "@gmail.com"));
        onView(withId(R.id.phoneEditText)).perform(replaceText("780-567-0000"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // make sure we still see the save button meaning that entering no name field didnt let the user leave the edit page
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
    }
    @Test
    public void testEditProfileEmptyEmailField(){
        //register a random profile
        String[] loginInfo = RegisterProfile();
        String email = loginInfo[0] + "@gmail.com";
        String name = loginInfo[2] + " " + loginInfo[3];

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int randomNum = random.nextInt(100);
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        // leave the email field empty and click save
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.clearText(),ViewActions.typeText("polar bears"));
        onView(withId(R.id.emailEditText)).perform(ViewActions.click(),ViewActions.clearText(), ViewActions.typeText(""));
        onView(withId(R.id.phoneEditText)).perform(replaceText("780-567-0000"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // make sure we still see the save button meaning that entering no name field didnt let the user leave the edit page
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
    }
}
