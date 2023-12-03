package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;
import static org.hamcrest.CoreMatchers.anything;

import com.example.househomey.testUtils.TestSetup;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class ViewItemFragmentTest extends TestSetup {
    private final Map<String, Object> mockData = ImmutableMap.of(
            "description", "Test Item",
            "acquisitionDate", new Timestamp(new Date()),
            "cost", "99.99",
            "make", "oldMake",
            "model", "oldModel",
            "serialNumber", "020202020",
            "comment", "original comment"
    );

    @Test
    public void testDetailsVisible() {
        waitFor(() -> onView(withId(R.id.total_count_text)).check(matches(withText("15"))));
        // Click to view the item page
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.view_item_make)).check(matches(withText("North Face")));
        onView(withId(R.id.view_item_model)).check(matches(withText("Recon")));
        onView(withId(R.id.view_item_serial_number)).check(matches(withText("7654982345678901")));
        onView(withId(R.id.view_item_cost)).check(matches(withText("52.00")));
        onView(withId(R.id.view_item_comment)).check(matches(withText("Spacious backpack for travel")));
    }

    @Test
    public void testDeleteItemWithNewUser() throws Exception {
        database.addTestItem(mockData);
        waitFor(() -> onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click()));
        onView(withId(R.id.delete_button)).perform(scrollTo(), click());

        waitFor(() -> onView(withId(R.id.total_count_text)).check(matches(withText("0"))));
    }
}
