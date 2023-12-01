package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import static org.hamcrest.CoreMatchers.anything;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

public class ApplyTagFragmentTest extends TestSetup {

    @Before
    public void waitForItems() {
        waitFor(() -> {
            onView(withId(R.id.item_list)).check(matches(isDisplayed()));
        });
        navigateToManageTagFragment();
    }
    public void navigateToManageTagFragment() {
        onView(withId(R.id.select_items_button)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_checkBox))
                .perform(click());
        onView(withId(R.id.action_tags)).perform(click());
    }

    @Test
    public void testSelectAndApply() {
        onView(withText("gaming")).perform(click());
        onView(withText("kitchen")).perform(click());
        onView(withText("APPLY")).perform(click());
        waitFor(() -> hasListLength(2));
    }

}
