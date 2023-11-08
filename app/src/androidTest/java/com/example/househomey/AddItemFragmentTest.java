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
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.househomey.testUtils.EspressoWait.waitForView;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.househomey.testUtils.MainActivityTestSetup;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddItemFragmentTest extends MainActivityTestSetup {

    @BeforeClass
    public static void dismissANRSystemDialog() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If the device is running in English Locale
        UiObject waitButton = device.findObject(new UiSelector().textContains("wait"));
        if (waitButton.exists()) {
            waitButton.click();
        }
    }

    @BeforeClass
    public static void dismissAppCrashSystemDialogIfShown() {
        try {
            UiDevice
                    .getInstance(InstrumentationRegistry.getInstrumentation())
                    .executeShellCommand(
                            "am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS");
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }

    @Before
    public void navigateToAddItemFragment() {
        onView(withId(R.id.action_add)).perform(click());
    }

    public void selectFirstDayOfMonth() {
        // Open date picker
        onView(withId(R.id.add_item_date)).perform(click());
        // Get the first day of this month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        // Format the date to match the MaterialDatePicker content description format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d");
        String formattedDate = dateFormat.format(firstDayOfMonth);
        // Select the day and click confirm
        onView(withContentDescription(formattedDate))
                .inRoot(RootMatchers.isDialog())
                .perform(click());
        onView(withId(com.google.android.material.R.id.confirm_button)).perform(click());
    }

    @Test
    public void testAddItemWithValidData() {
        String itemDescription = "Test Item";
        // Add brief description
        onView(withId(R.id.add_item_description)).perform(replaceText(itemDescription), closeSoftKeyboard());
        // Add acquisition date
        selectFirstDayOfMonth();
        // Add an estimated cost
        onView(withId(R.id.add_item_cost)).perform(replaceText("100"), closeSoftKeyboard());
        // TODO: Add other relevant fields
        // Click the confirm button to add the item
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Check that we switch back to home page
        waitForView(withId(R.id.item_list));
        // Check that the item is present in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_description_text))
                .check(matches(withText(itemDescription)));
    }
}
