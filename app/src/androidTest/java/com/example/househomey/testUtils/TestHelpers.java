package com.example.househomey.testUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.IdRes;

import com.example.househomey.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/*
 * Helper methods for Espresso testing in Android applications.
 *
 * @author Owen Cooke
 */
public class TestHelpers {
    private static final long TIMEOUT = 10000; // Timeout in milliseconds
    private static final long POLLING_INTERVAL = 500; // Polling interval in milliseconds

    /*
     * Wait for a lambda function to execute without error within a specified timeout.
     * Intended use is for wrapping Espresso statements that rely on Firebase changes,
     * which potentially take additional time to update.
     *
     * @param lambda The Espresso statement to execute.
     */
    public static void waitFor(Runnable lambda) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        while (elapsedTime < TIMEOUT) {
            try {
                lambda.run();
                return; // Statement executed successfully, exit
            } catch (Throwable ignore) {
            }
            try {
                Thread.sleep(POLLING_INTERVAL);
            } catch (InterruptedException ignore) {
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Retry one last time, otherwise error out with added description
        try {
            lambda.run();
        } catch (Throwable e) {
            throw new AssertionError("Espresso statement did not succeed within the timeout. " + e.getMessage());
        }
    }

    /*
     * Checks if the total count text displayed on home page matches expected count.
     *
     * @param expectedCount The expected number of child elements in the list view.
     */
    public static void hasListLength(int expectedCount) {
        onView(withId(R.id.total_count_text)).check(matches(withText(String.valueOf(expectedCount))));
    }

    /*
     * Types text into an EditText field.
     * Presses the IME action button, for actions like "Done," "Search," or "Next"
     * that follow entering text. Also ensures the keyboard is closed.
     *
     * @param viewId The resource ID of the EditText view.
     * @param text   The text to be entered into the view.
     */
    public static void enterText(@IdRes int viewId, String text) {
        onView(withId(viewId)).perform(scrollTo(), clearText(), typeText(text), pressImeActionButton(), closeSoftKeyboard());
    }

    /*
     * Returns a Bitmap image, loaded from a resource file
     *
     * @param activity The context of the current activity.
     * @param resourceId   The R.id of the image to be turned into a Bitmap object.
     */
    public static Bitmap mockImageBitmap(Activity activity, int resourceId) {
        return BitmapFactory.decodeResource(activity.getResources(), resourceId);
    }

    /*
     * Returns a Uri for an image, loaded from a resource file.
     *
     * @param resourceId   The R.id of the image to get a Uri reference to.
     */
    public static Uri mockImageUri(int resourceId) {
        return Uri.parse("android.resource://com.example.househomey/" + resourceId);
    }

    /*
     * Sets the Material DatePicker date for a specified date text input.
     * Uses text input mode of the DatePicker.
     *
     * @param id   The resource ID of the view element for which the date is being set.
     * @param date The date string to be set.
     */
    public static void pickDate(int id, String date) {
        onView(withId(id)).perform(click());
        onView(withContentDescription("Switch to text input mode")).perform(click());
        onView(Matchers.allOf(childAtPosition(childAtPosition(
                        withId(com.google.android.material.R.id.mtrl_picker_text_input_date), 0), 0),
                isDisplayed())).perform(replaceText(date), closeSoftKeyboard());
        onView(withText("OK")).perform(click());
    }

    /*
     * Custom Matcher for locating a child at a specific position within a parent
     *
     * @param parentMatcher The Matcher<View> used to match the parent View or ViewGroup
     * @param position      The position of the child View within the parent.
     * @return A Matcher<View> instance for finding a child View at the specified position.
     */
    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
