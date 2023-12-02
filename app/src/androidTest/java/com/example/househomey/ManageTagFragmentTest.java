package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import static org.hamcrest.CoreMatchers.anything;


import androidx.test.espresso.Espresso;

import com.example.househomey.testUtils.TestSetup;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class ManageTagFragmentTest extends TestSetup {

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
    public void createData() {
        database.addTestItem(mockData);
        waitFor(() -> hasListLength(1));
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
        onView(withText("MANAGE")).perform(click());
    }

    @Test
    public void testCreateNewTagWithNewUser() {
        String tag1 = "Tag1";
        onView(withId(R.id.tag_edit_text)).perform(clearText(), typeText(tag1));
        onView(withId(R.id.add_tag_button)).perform(click());
        waitFor(() -> onView(withText(tag1)).check(matches(isDisplayed())));

    }

    @Test
    public void testDoneButtonWithNewUser() {
        onView(withText("DONE")).perform(click());
        waitFor(() -> onView(withText("APPLY")).check(matches(isDisplayed())));
    }


}
