package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.waitForView;
import static org.hamcrest.CoreMatchers.anything;

import com.example.househomey.testUtils.TestSetup;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class EditItemFragmentTest extends TestSetup {
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
    public void navigateToEditItemFragment() throws Exception {
        // Create mock initial item in DB
        database.addMockItem(mockData);
        // Click to view the item page
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.action_view))
                .perform(click());
        // Click on edit button
        onView(withId(R.id.edit_button)).perform(click());
    }

    @Test
    public void testEditItemWithNewUser() {
        String cost = "222.22";
        String make = "NewMake";
        String comment = "this is an edited comment";
        // Edit the cost/comment/make
        enterText(R.id.add_item_cost, cost);
        enterText(R.id.add_item_make, make);
        enterText(R.id.add_item_comment, comment);
        // Click the confirm button to make edits
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Check that edits appear on view item page
        waitForView(withId(R.id.view_item_make));
        onView(withId(R.id.view_item_cost)).check(matches(withText(cost)));
        onView(withId(R.id.view_item_comment)).check(matches(withText(comment)));
        onView(withId(R.id.view_item_make)).check(matches(withText(make)));
        // Check that the unedited fields remain the same
        onView(withId(R.id.view_item_serial_number)).check(matches(withText(mockData.get("serialNumber").toString())));
        onView(withId(R.id.view_item_model)).check(matches(withText(mockData.get("model").toString())));
    }

    @Test
    public void testBackButtonGoesToViewItemPage() {
        onView(withId(R.id.add_item_back_button)).perform(click());
        waitForView(withId(R.id.view_item_make));
    }
}
