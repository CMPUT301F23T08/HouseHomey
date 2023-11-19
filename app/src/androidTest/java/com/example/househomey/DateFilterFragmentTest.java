package com.example.househomey;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import com.example.househomey.testUtils.TestSetup;

public class DateFilterFragmentTest extends TestSetup  {
    @Before
    public void waitForItems() {
        waitFor(() -> {
            onView(withId(R.id.item_list)).check(matches(isDisplayed()));
        });
        navigateToDateRangeFilter();
    }

    @Test
    public void testDateFilterWithResults() {
        setDateFilter(R.id.start_date_filter, "10/01/2023");
        setDateFilter(R.id.end_date_filter, "10/30/2023");
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(3));
    }

    @Test
    public void testDateFilterWithNoResults() {
        setDateFilter(R.id.start_date_filter, "10/01/2001");
        setDateFilter(R.id.end_date_filter, "10/01/2001");
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(0));
    }

    @Test
    public void testDateFilterWithOneDateFilled() {
        setDateFilter(R.id.start_date_filter, "10/01/2023");
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(4));
    }

    /**
     * Navigates to the Date Range filter in the application.
     * This method clicks on the filter dropdown button and selects the "Date Range" option.
     */
    public void navigateToDateRangeFilter() {
        onView(withId(R.id.filter_dropdown_button)).perform(click());
        onView(withText("Date Range")).perform(click());
    }

    /**
     * Sets a date filter for a specified view element using Espresso.
     *
     * @param id   The resource ID of the view element for which the date filter is being set.
     * @param date The date string to be set in the date filter.
     *             This method clicks on the specified view element, switches to text input mode,
     *             enters the provided date, and confirms the date selection by clicking "OK".
     */
    private void setDateFilter(int id, String date) {
        onView(withId(id)).perform(click());
        onView(withContentDescription("Switch to text input mode")).perform(click());
        onView(
                Matchers.allOf(childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.mtrl_picker_text_input_date),
                                        0),
                                0),
                        isDisplayed())).perform(replaceText(date), closeSoftKeyboard());

        onView(withText("OK")).perform(click());
    }

    /**
     * Custom Matcher for locating a child {@link View} at a specific position within a parent
     * {@link ViewGroup}.
     *
     * @param parentMatcher The {@link Matcher<View>} used to match the parent {@link View} or {@link ViewGroup}.
     * @param position      The position of the child {@link View} within the parent.
     * @return A {@link Matcher<View>} instance for finding a child {@link View} at the specified position.
     */
    Matcher<View> childAtPosition(
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
