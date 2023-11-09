package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.waitForView;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.matcher.RootMatchers;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddItemFragmentTest extends TestSetup {
    @Before
    public void navigateToAddItemFragment() {
        onView(withId(R.id.action_add)).perform(click());
    }

    public String selectFirstDayOfMonth() {
        onView(withId(R.id.add_item_date)).perform(click());
        // Get the first day of the month matching MaterialDatePicker content description format
        LocalDate currentDate = LocalDate.now().withDayOfMonth(1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d");
        String formattedDate = currentDate.format(dateFormat);
        // Select the day and click confirm
        onView(withContentDescription(formattedDate))
                .inRoot(RootMatchers.isDialog())
                .perform(click());
        onView(withId(com.google.android.material.R.id.confirm_button)).perform(click());
        // Return HomePage's formatted date
        return currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Test
    public void testAddItemWithValidData() {
        // Add acquisition date
        String acquisitionDate = selectFirstDayOfMonth();
        // Add required description and estimated cost
        String itemDescription = "Test Item";
        String estimatedCost = "99.99";
        enterText(R.id.add_item_description, itemDescription);
        enterText(R.id.add_item_cost, estimatedCost);
        // Add values to other non-required fields
        enterText(R.id.add_item_make, "MyMake");
        enterText(R.id.add_item_model, "MyModel");
        enterText(R.id.add_item_serial_number, "1234567890");
        onView(withId(R.id.add_item_comment)).perform(scrollTo());
        waitForView(withId(R.id.add_item_comment));
        enterText(R.id.add_item_comment, "this is a comment");
        // Click the confirm button to add the item
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Check that we switch back to home page
        waitForView(withId(R.id.item_list));
        hasListLength(1);
        // Check that the item in list matches description, date, and cost
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_description_text))
                .check(matches(withText(itemDescription)));
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_text))
                .check(matches(withText(acquisitionDate + " | $" + estimatedCost)));
    }

    @Test
    public void testSubmitWithRequiredEmpty() {
        String defaultErrorMsg = "This field is required";
        // Click the add button with empty form
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Check that error messages are displayed in TextInputLayouts for required fields
        onView(withId(R.id.add_item_description_layout)).check(matches(hasDescendant(withText(defaultErrorMsg))));
        onView(withId(R.id.add_item_cost_layout)).check(matches(hasDescendant(withText(defaultErrorMsg))));
        onView(withId(R.id.add_item_date_layout)).check(matches(hasDescendant(withText(defaultErrorMsg))));
    }

    @Test
    public void testBackButtonGoesToHomePage() {
        onView(withId(R.id.add_item_back_button)).perform(click());
        // Check that we switch back to home page with empty list
        waitForView(withId(R.id.item_list));
        hasListLength(0);
    }

    @Test
    public void testRoundCostTo2Decimals() {
        enterText(R.id.add_item_cost, "99.9852323324");
        onView(withId(R.id.add_item_cost)).check(matches(withText("99.99")));
    }
}