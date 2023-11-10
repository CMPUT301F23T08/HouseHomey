package com.example.househomey;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import com.example.househomey.testUtils.TestSetup;
import org.junit.Before;
import org.junit.Test;

public class MakeFilterFragmentTest extends TestSetup {
    @Before
    public void waitForItems() {
        waitFor(() -> {
            onView(withId(R.id.item_list)).check(matches(isDisplayed()));
        });
        navigateToMakeFilter();
    }

    public void navigateToMakeFilter() {
        onView(withId(R.id.filter_dropdown_button)).perform(click());
        onView(withText("Make")).perform(click());
    }

    @Test
    public void testMakeFilter() {
        String make1 = "Apple";
        String make2 = "Samsung";

        onView(withId(R.id.make_filter)).perform(clearText(), typeText(make1));
        onView(withText("Apply")).perform(click());
        waitFor(() -> hasListLength(3));
        navigateToMakeFilter();
        onView(withId(R.id.make_filter)).perform(clearText(), typeText(make2));
        onView(withText("Apply")).perform(click());
        waitFor(() -> hasListLength(3));
    }

}
