package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.EspressoWait.waitForView;
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
        onView(withId(R.id.add_item_description)).perform(replaceText(itemDescription), closeSoftKeyboard());
        onView(withId(R.id.add_item_cost)).perform(replaceText(estimatedCost), closeSoftKeyboard());
        // Add values to other non-required fields
        onView(withId(R.id.add_item_make)).perform(replaceText("MyMake"), closeSoftKeyboard());
        onView(withId(R.id.add_item_model)).perform(replaceText("MyModel"), closeSoftKeyboard());
        onView(withId(R.id.add_item_serial_number)).perform(replaceText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.add_item_comment)).perform(replaceText("this is a comment"), closeSoftKeyboard());
        // Click the confirm button to add the item
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Check that we switch back to home page
        waitForView(withId(R.id.item_list));
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
}