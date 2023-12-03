package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import org.junit.Before;
import org.junit.Test;

public class SignUpFragmentTest {
    private ActivityScenario<SignInActivity> activityScenario;

    @Before
    public void setUp() {
        // Launch the activity before each test
        activityScenario = ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.signin_redirect)).perform(click());
    }

    @Test
    public void testUsername() {
        onView(withId(R.id.signup_username)).perform(typeText("antonio2"));
        onView(withId(R.id.signup_username)).perform(clearText());
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("username cannot be empty")));
        onView(withId(R.id.signup_username)).perform(typeText("antonio2$"));
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("only alphanumeric, underscore, or period")));
    }

    @Test
    public void testEmail() {
        onView(withId(R.id.signup_email)).perform(typeText("antonio2"));
        onView(withId(R.id.signup_email)).check(matches(hasErrorText("not a valid email")));
        onView(withId(R.id.signup_email)).perform(clearText());
        onView(withId(R.id.signup_email)).check(matches(hasErrorText("email cannot be empty")));
    }

    @Test
    public void testConfirmedPassword() {
        onView(withId(R.id.signup_password)).perform(typeText("123456"));
        onView(withId(R.id.signup_confirm_password)).perform(typeText("12345"));
        onView(withId(R.id.signup_confirm_password)).check(matches(hasErrorText("passwords do not match")));
        onView(withId(R.id.signup_confirm_password)).perform(clearText());
        onView(withId(R.id.signup_confirm_password)).check(matches(hasErrorText("password cannot be empty")));
    }

    @Test
    public void testRegister() {
        onView(withId(R.id.signup_username)).perform(typeText("ESPRESSO_GENERAL_USER"));
        onView(withId(R.id.signup_email)).perform(typeText("espresso_general_user@example.com"));
        onView(withId(R.id.signup_password)).perform(typeText("12345"), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.signup_confirm_password)).perform(typeText("12345"), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.signup_username)).check(matches(hasErrorText("username already exists")));
        onView(withId(R.id.signup_username)).perform(clearText(), typeText("ESPRESSO_GENERAL_USER1"), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.signup_password)).check(matches(hasErrorText("The given password is invalid. [ Password should be at least 6 characters ]")));
        onView(withId(R.id.signup_confirm_password)).check(matches(hasErrorText("The given password is invalid. [ Password should be at least 6 characters ]")));
        onView(withId(R.id.signup_password)).perform(clearText(), typeText("123456"), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.signup_confirm_password)).perform(clearText(), typeText("123456"), pressImeActionButton(), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.signup_email)).check(matches(hasErrorText("The email address is already in use by another account.")));
    }
}
