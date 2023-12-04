package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import androidx.test.core.app.ActivityScenario;

import com.example.househomey.signin.SignInActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserProfileTest {
    private ActivityScenario<SignInActivity> activityScenario;

    @Before
    public void setUp() {
        // Launch the activity before each test
        activityScenario = ActivityScenario.launch(SignInActivity.class);
    }

    @Test
    public void testUserProfile() {
        enterText(R.id.signin_username, "ESPRESSO_GENERAL_USER");
        enterText(R.id.signin_password, "123456");
        onView(withId(R.id.signin_button)).perform(click());
        waitFor(() -> onView(withId(R.id.action_profile)).perform(click()));
        onView(withId(R.id.user_profile_username)).check(matches(withText("ESPRESSO_GENERAL_USER")));
        onView(withId(R.id.user_profile_email)).check(matches(withText("espresso_general_user@example.com")));
    }

    @After
    public void tearDown() {
        activityScenario.close();
    }
}
