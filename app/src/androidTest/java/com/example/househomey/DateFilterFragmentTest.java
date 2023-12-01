package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.pickDate;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

public class DateFilterFragmentTest extends TestSetup  {
    @Before
    public void waitForItems() {
        waitFor(() -> onView(withId(R.id.item_list)).check(matches(isDisplayed())));
        navigateToDateRangeFilter();
    }

    @Test
    public void testDateFilterWithResults() {
        pickDate(R.id.start_date_filter, "10/01/2023");
        pickDate(R.id.end_date_filter, "10/30/2023");
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(3));
    }

    @Test
    public void testDateFilterWithNoResults() {
        pickDate(R.id.start_date_filter, "10/01/2001");
        pickDate(R.id.end_date_filter, "10/01/2001");
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(0));
    }

    @Test
    public void testDateFilterWithOneDateFilled() {
        pickDate(R.id.start_date_filter, "10/01/2023");
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

}
