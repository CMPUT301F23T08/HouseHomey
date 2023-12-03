package com.example.househomey;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.mockImageUri;
import static com.example.househomey.testUtils.TestHelpers.waitFor;
import static org.hamcrest.CoreMatchers.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

public class BarcodeScannerTest extends TestSetup {
    @Before
    public void navigateToAddItemFragment() {
        onView(withId(R.id.action_add)).perform(click());
    }

    @Test
    public void testSetSerialNumber() {
        // Mock a result for the system's gallery
        Intent resultData = new Intent();
        resultData.setData(mockImageUri(R.raw.barcode_test));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        onView(withId(R.id.add_item_scan_button)).perform(scrollTo());
        onView(withId(R.id.add_item_scan_button)).perform(click());
        onView(withId(R.id.barcode_button)).perform(click());

        // Click the gallery option and ensure intent was fired
        onView(withId(R.id.gallery_button)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));

        waitFor(() -> onView(withText("Serial Number")).perform(click()));

        // Check that the serial number
        onView(withId(R.id.add_item_serial_number)).check(matches(withText("605388926463")));
    }

    @Test
    public void testSetDescription() {
        // Mock a result for the system's gallery
        Intent resultData = new Intent();
        resultData.setData(mockImageUri(R.raw.barcode_test));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        onView(withId(R.id.add_item_scan_button)).perform(scrollTo());
        onView(withId(R.id.add_item_scan_button)).perform(click());
        onView(withId(R.id.barcode_button)).perform(click());

        // Click the gallery option and ensure intent was fired
        onView(withId(R.id.gallery_button)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));

        waitFor(() -> onView(withText("Description")).perform(click()));

        // Check that the serial number
        onView(withId(R.id.add_item_description)).check(matches(withText("605388926463")));
    }

    @Test
    public void testQRCodeScan() {
        // Mock a result for the system's gallery
        Intent resultData = new Intent();
        resultData.setData(mockImageUri(R.raw.househomey_qrcode));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        onView(withId(R.id.add_item_scan_button)).perform(scrollTo());
        onView(withId(R.id.add_item_scan_button)).perform(click());
        onView(withId(R.id.barcode_button)).perform(click());

        // Click the gallery option and ensure intent was fired
        onView(withId(R.id.gallery_button)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));

        waitFor(() -> onView(withText("YES")).perform(click()));

        // Check that the serial number
        onView(withId(R.id.add_item_description)).check(matches(withText(
                "https://me-qr.com/l/HouseHomeyGithubPage")));
    }
}
