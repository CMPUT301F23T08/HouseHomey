package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;

import com.example.househomey.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

public class SignUpFragmentTest {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        enterText(R.id.signup_password, "123456");
        enterText(R.id.signup_confirm_password, "12345");
        onView(withId(R.id.signup_confirm_password)).check(matches(hasErrorText("passwords do not match")));
    }

    @Test
    public void testRegister() {
        onView(withId(R.id.signup_username)).perform(typeText("ESPRESSO_GENERAL_USER"));
        onView(withId(R.id.signup_email)).perform(typeText("espresso_general_user@example.com"));
        enterText(R.id.signup_password, "12345");
        enterText(R.id.signup_confirm_password, "12345");
        onView(withId(R.id.signup_button)).perform(click());
        waitFor(() -> onView(withId(R.id.signup_username)).check(matches(hasErrorText("username already exists"))));
        enterText(R.id.signup_username, "a");
        onView(withId(R.id.signup_button)).perform(click());
        waitFor(() -> onView(withId(R.id.signup_password)).check(matches(hasErrorText("The given password is invalid. [ Password should be at least 6 characters ]"))));
        onView(withId(R.id.signup_confirm_password)).check(matches(hasErrorText("The given password is invalid. [ Password should be at least 6 characters ]")));
        enterText(R.id.signup_password, "123456");
        enterText(R.id.signup_confirm_password, "123456");
        onView(withId(R.id.signup_button)).perform(click());
        waitFor(() -> onView(withId(R.id.signup_email)).check(matches(hasErrorText("The email address is already in use by another account."))));
        enterText(R.id.signup_email, "a@example.com");
        onView(withId(R.id.signup_button)).perform(click());
        waitFor(() -> onView(withId(R.id.signin_username)).check(matches(isDisplayed())));
        enterText(R.id.signin_username, "a");
        enterText(R.id.signin_password, "123456");
        onView(withId(R.id.signin_button)).perform(click());
        waitFor(() -> hasListLength(0));
        DocumentReference docRef = db.collection("user").document("a");
        docRef.delete().addOnSuccessListener(a -> Objects.requireNonNull(auth.getCurrentUser()).delete().addOnSuccessListener(aVoid -> Log.d("ESP-TEST", "Firebase Auth user deleted successfully"))
                .addOnFailureListener(e -> Log.e("ESP-TEST", "Could not delete Firebase Auth user: " + e.getMessage()))).addOnFailureListener(a -> Log.e("ESP-TEST", "Document failed to delete!"));
    }

    @After
    public void tearDown() {
        activityScenario.close();
    }
}
