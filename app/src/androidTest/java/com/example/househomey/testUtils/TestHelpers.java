package com.example.househomey.testUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.annotation.IdRes;

import com.example.househomey.R;

import org.hamcrest.Matcher;

/**
 * Helper methods for Espresso testing in Android applications.
 * @author Owen Cooke
 */
public class TestHelpers {
    private static final long TIMEOUT = 5000; // Timeout in milliseconds
    private static final long POLLING_INTERVAL = 500; // Polling interval in milliseconds

    /**
     * Wait for a view to be displayed within a specified timeout (5s).
     *
     * @param viewMatcher The Matcher for the view to wait for.
     */
    public static void waitForView(Matcher<View> viewMatcher) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        // Try to match multiple times during timeout
        while (elapsedTime < TIMEOUT) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                return; // View is displayed, exit
            } catch (Exception ignore) {
            }
            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException ignore) {
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Try one last time, otherwise error out with added description
        try {
            onView(viewMatcher).check(matches(isDisplayed()));
        } catch (Exception e) {
            throw new AssertionError("View matching the given Matcher was not displayed within the timeout. " + e);
        }
    }

    /**
     * Check if the list of Items has the expected number of Items.
     *
     * @param expectedCount The expected number of child elements in the list view.
     */
    public static void hasListLength(int expectedCount) {
        onView(withId(R.id.item_list)).check(matches(hasChildCount(expectedCount)));
    }

    /**
     * Types text into an EditText field.
     * Presses the IME action button, for actions like "Done," "Search," or "Next"
     * that follow entering text. Also ensures the keyboard is closed.
     *
     * @param viewId The resource ID of the EditText view.
     * @param text   The text to be entered into the view.
     */
    public static void enterText(@IdRes int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text), pressImeActionButton(), closeSoftKeyboard());
    }
}