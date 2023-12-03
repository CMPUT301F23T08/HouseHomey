package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

public class TagFilterFragmentTest extends TestSetup {
    @Before
    public void waitForItems() {
        waitFor(() -> {
            onView(withId(R.id.item_list)).check(matches(isDisplayed()));
        });
        navigateToTagFilter();
    }

    public void navigateToTagFilter() {
        onView(withId(R.id.filter_dropdown_button)).perform(click());
        onView(withText("Tags")).perform(click());
    }

    @Test
    public void testMultipleTagFilter() {
        onView(withText("gaming")).perform(click());
        onView(withText("kitchen")).perform(click());
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(2));
    }

    @Test
    public void testOneTagFilter() {
        onView(withText("electronics")).perform(click());
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(11));
    }

    @Test
    public void testNoTagFilter() {
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(15));
    }
}