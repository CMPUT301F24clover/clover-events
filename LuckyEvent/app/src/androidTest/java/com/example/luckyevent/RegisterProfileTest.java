package com.example.luckyevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.luckyevent.activities.EntrantSignUpActivity;
import com.example.luckyevent.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class RegisterProfileTest {

    //Ensure the tests start in LoginActivity
    @Rule
    public ActivityScenarioRule<LoginActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);


    public void signupToRegisterProfile(String userName, String pass, String firstName, String lastName){
        // click the signup button
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());

        // find the SignUpUsernameInput meaning where in the sign up page
        onView(withId(R.id.SignUpUsernameInput)).check(matches(isDisplayed()));

        // sign up and input signup fields
        onView(withId(R.id.SignUpUsernameInput)).perform(ViewActions.typeText(userName));
        onView(withId(R.id.SignUpPasswordInput)).perform(ViewActions.typeText(pass));
        onView(withId(R.id.SignUpFirstNameInput)).perform(ViewActions.typeText(firstName));
        onView(withId(R.id.SignUpLastNameInput)).perform(ViewActions.typeText(lastName));

        //click signup
        onView(withId(R.id.SignUpButton)).perform(ViewActions.click());

        // my tests where running to fast so i paused for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // check to see if we see the bottom navigation bar meaning were were able to signup
        onView(withId(R.id.bottomNavigationViewEntrant)).check(matches(isDisplayed()));

        // click the profile icon on the navigation bar to go to register profile
        onView(withId(R.id.profile_item)).perform(ViewActions.click());
        // check to see the name edit text field meaning we navigated
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()));
    }



    @Test
    public void testEmptyNameEmailFields(){
        // generate randon info to signup with
        Random random = new Random();
        int randomNum = random.nextInt(100);
        String userName = "kassycool" + randomNum;
        String firstName = "kassy" + randomNum;
        String lastName = "brian" + randomNum;
        signupToRegisterProfile(userName,"test123",firstName,lastName);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try leaving the register fields empty
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.typeText(""));
        onView(withId(R.id.emailEditText)).perform(ViewActions.click(), ViewActions.typeText(""));
        // try clicking register
        onView(withId(R.id.RegisterButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if we still see the register button after 3 seconds that means we werent able to register (test passes)
        onView(withId(R.id.RegisterButton)).check(matches(isDisplayed()));
    }
    @Test
    public void testCorrectNameField (){
        Random random = new Random();
        int randomNum = random.nextInt(100);
        String userName = "jamescool" + randomNum;
        String firstName = "james" + randomNum;
        String lastName = "leo" + randomNum;
        signupToRegisterProfile(userName,"test123",firstName,lastName);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // when we first register the name should match the one we signed up with, if it doesnt it wont let you register
        // try using the wrong name and hit register
        onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.typeText("wrong name"));
        onView(withId(R.id.emailEditText)).perform(ViewActions.click(), ViewActions.typeText("panda@gmail.com"));
        onView(withId(R.id.RegisterButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if we still see the register button after 3 seconds that means we werent able to register (test passes)
        onView(withId(R.id.RegisterButton)).check(matches(isDisplayed()));
    }
    @Test
    public void testRegisterProfile(){
        Random random = new Random();
        int randomNum = random.nextInt(100);
        String userName = "pandacool" + randomNum;
        String firstName = "panda" + randomNum;
        String lastName = "bear" + randomNum;
        String name = firstName + " " + lastName;
        signupToRegisterProfile(userName,"test123",firstName,lastName);
         onView(withId(R.id.nameEditText)).check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try registering with the correct name and a non empty email field
         onView(withId(R.id.nameEditText)).perform(ViewActions.click(), ViewActions.typeText(name));
         onView(withId(R.id.emailEditText)).perform(ViewActions.click(), ViewActions.typeText("panda@gmail.com"));
         onView(withId(R.id.RegisterButton)).perform(ViewActions.click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

         // if we see the edit button after registering that means we were able to register and got brought to the view page
        onView(withId(R.id.editButton)).check(matches(isDisplayed()));
    }
}
