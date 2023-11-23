package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.matcher.RootMatchers;

import com.example.househomey.testUtils.TestSetup;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;


public class SelectDeleteTest extends TestSetup {

    private final Map<String, Object> mockData = ImmutableMap.of(
            "description", "Test Item",
            "acquisitionDate", new Timestamp(new Date()),
            "cost", "99.99",
            "make", "oldMake",
            "model", "oldModel",
            "serialNumber", "020202020",
            "comment", "original comment"
    );

    @Before
// Create mock initial item in DB
    public void createData() throws Exception {
        database.addTestItem(mockData);
        database.addTestItem(mockData);
        database.addTestItem(mockData);
        waitFor(() -> onView(withId(R.id.total_count_text)).check(matches(withText("3"))));
    }

    @Test
    public void testSelectDeleteWithNewUser() {
        // Add required description and estimated cost
        onView(withId(R.id.select_items_button)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_checkBox))
                .perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_checkBox))
                .perform(click());

        onView(withId(R.id.action_delete)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.total_count_text)).check(matches(withText("1")));
    }

}
