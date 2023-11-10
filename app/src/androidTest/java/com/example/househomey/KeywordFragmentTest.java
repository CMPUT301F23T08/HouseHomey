package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitFor;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.househomey.R;
import com.example.househomey.testUtils.TestSetup;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class KeywordFragmentTest extends TestSetup {
    private final Map<String, Object> mockData1 = ImmutableMap.of(
            "description", "Test Item1",
            "acquisitionDate", new Timestamp(new Date()),
            "cost", "99.99",
            "make", "oldMake",
            "model", "oldModel",
            "serialNumber", "020202020",
            "comment", "original comment"
    );

    private final Map<String, Object> mockData2 = ImmutableMap.of(
            "description", "Test Item2",
            "acquisitionDate", new Timestamp(new Date()),
            "cost", "99.99",
            "make", "oldMake",
            "model", "oldModel",
            "serialNumber", "020202020",
            "comment", "original comment"
    );

    private final Map<String, Object> mockData3 = ImmutableMap.of(
            "description", "Test Item3",
            "acquisitionDate", new Timestamp(new Date()),
            "cost", "99.99",
            "make", "oldMake",
            "model", "oldModel",
            "serialNumber", "020202020",
            "comment", "original comment"
    );

    public void navigateToFilter() {
        onView(withId(R.id.filter_dropdown_button)).perform(click());
        onView(withText("Description Keywords")).perform(click());
    }

    @Before
    public void setupData() throws Exception {
        // Create mock initial item in DB
        database.addTestItem(mockData1);
        database.addTestItem(mockData2);
        database.addTestItem(mockData3);
        navigateToFilter();
    }

    @Test
    public void testKeywordFilterWithNewUser() {
        String item1 = "Item1";
        String item2 = "Item2";
        String item3 = "";

        onView(withId(R.id.keyword_edit_text)).perform(clearText(), typeText(item1));
        onView(withId(R.id.add_keyword_button)).perform(click());
        onView(withText("Apply")).perform(click());
        waitFor(()-> hasListLength(1));
        navigateToFilter();
        onView(withId(R.id.keyword_edit_text)).perform(clearText(), typeText(item2));
        onView(withId(R.id.add_keyword_button)).perform(click());
        onView(withText("Apply")).perform(click());
        waitFor(()-> hasListLength(2));
        navigateToFilter();
        onView(withId(R.id.keyword_edit_text)).perform(clearText(), typeText(item3));
        onView(withId(R.id.add_keyword_button)).perform(click());
        onView(withText("Apply")).perform(click());
        waitFor(()-> hasListLength(2));
    }

    @Test
    public void testKeywordFilterEmptyStringWithNewUser(){
        String item3 = "";
        onView(withId(R.id.keyword_edit_text)).perform(clearText(), typeText(item3));
        onView(withId(R.id.add_keyword_button)).perform(click());
        onView(withText("Apply")).perform(click());
        waitFor(()-> hasListLength(3));
    }
}
