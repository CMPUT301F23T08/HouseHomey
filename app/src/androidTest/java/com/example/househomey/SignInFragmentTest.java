package com.example.househomey;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import androidx.test.core.app.ActivityScenario;

import com.example.househomey.signin.SignInActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SignInFragmentTest {
    private ActivityScenario<SignInActivity> activityScenario;

    @Before
    public void setUp() {
        // Launch the activity before each test
        activityScenario = ActivityScenario.launch(SignInActivity.class);
    }

    @Test
    public void testAllEmpty() {
        onView(withId(R.id.signin_button)).perform(scrollTo(), click());
        onView(withId(R.id.signin_username)).check(matches(hasErrorText("username cannot be empty")));
        onView(withId(R.id.signin_password)).check(matches(hasErrorText("password cannot be empty")));
    }


    @Test
    public void testUsername() {
        onView(withId(R.id.signin_username)).perform(typeText("antonio2"));
        onView(withId(R.id.signin_username)).perform(clearText());
        onView(withId(R.id.signin_username)).check(matches(hasErrorText("username cannot be empty")));
        onView(withId(R.id.signin_username)).perform(typeText("antonio2$"));
        onView(withId(R.id.signin_username)).check(matches(hasErrorText("only alphanumeric, underscore, or period")));
    }

    @Test
    public void testPassword() {
        onView(withId(R.id.signin_password)).perform(typeText("123456"));
        onView(withId(R.id.signin_password)).perform(clearText());
        onView(withId(R.id.signin_password)).check(matches(hasErrorText("password cannot be empty")));
    }

    @Test
    public void testLogin() {
        enterText(R.id.signin_username, "ESPRESSO_GENERAL_USER");
        enterText(R.id.signin_password, "123456");
        onView(withId(R.id.signin_button)).perform(click());
        waitFor(()->hasListLength(15));
    }

    @After
    public void tearDown() {
        activityScenario.close();
    }

}
