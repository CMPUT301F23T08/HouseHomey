package com.example.househomey;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.househomey.testUtils.TestHelpers.enterText;
import static com.example.househomey.testUtils.TestHelpers.hasListLength;
import static com.example.househomey.testUtils.TestHelpers.mockImageBitmap;
import static com.example.househomey.testUtils.TestHelpers.mockImageUri;
import static com.example.househomey.testUtils.TestHelpers.pickDate;
import static com.example.househomey.testUtils.TestHelpers.waitFor;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import com.example.househomey.testUtils.TestSetup;

import org.junit.Before;
import org.junit.Test;

public class AddItemFragmentTest extends TestSetup {
    @Before
    public void navigateToAddItemFragment() {
        onView(withId(R.id.action_add)).perform(click());
    }

    @Test
    public void testAddItemWithNewUser() {
        String itemDescription = "Test Item";
        String estimatedCost = "99.99";
        String itemMake = "MyMake";
        // Add required fields
        enterText(R.id.add_item_description, itemDescription);
        pickDate(R.id.add_item_date, "10/01/2023");
        enterText(R.id.add_item_cost, estimatedCost);
        // Add values to other non-required fields
        enterText(R.id.add_item_make, itemMake);
        enterText(R.id.add_item_model, "MyModel");
        enterText(R.id.add_item_serial_number, "1234567890");
        enterText(R.id.add_item_comment, "this is a comment");
        // Click the confirm button to add the item
        onView(withId(R.id.add_item_confirm_button)).perform(click());
        // Wait for Firebase to update list on home page
        waitFor(() -> hasListLength(1));
        // Check that the item in list matches description, date, and cost
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_description_text))
                .check(matches(withText(itemDescription)));
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_date_text))
                .check(matches(withText("2023-10-01")));
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_make_text))
                .check(matches(withText(itemMake)));
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .onChildView(withId(R.id.item_cost_text))
                .check(matches(withText("$"+estimatedCost)));
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
    public void testRoundCostTo2Decimals() {
        enterText(R.id.add_item_cost, "99.9852323324");
        onView(withId(R.id.add_item_cost)).check(matches(withText("99.99")));
    }

    @Test
    public void testAddPhotoFromCamera() {
        // Mock a result for the system's camera
        Intent resultData = new Intent();
        resultData.putExtra("data", mockImageBitmap(mainActivity, R.raw.classic_guitar));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        // Check that there are no photos and open menu
        onView(withId(R.id.add_photo_grid)).check(matches(hasChildCount(1)));
        onView(withId(R.id.add_photo_button)).perform(click());

        // Click the camera option and ensure intent was fired
        onView(withId(R.id.camera_button)).perform(click());
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));

        // Check that the photo was added
        onView(withId(R.id.add_photo_grid)).check(matches(hasChildCount(2)));
        onView(withId(R.id.add_photo_grid)).check(matches(hasDescendant(allOf(withId(R.id.gallery_image_view), isDisplayed()))));
    }

    @Test
    public void testAddPhotoFromGallery() {
        // Mock a result for the system's gallery
        Intent resultData = new Intent();
        resultData.setData(mockImageUri(R.raw.shoes));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Check that there are no photos and open menu
        onView(withId(R.id.add_photo_grid)).check(matches(hasChildCount(1)));
        onView(withId(R.id.add_photo_button)).perform(click());

        // Click the gallery option and ensure intent was fired
        onView(withId(R.id.gallery_button)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));

        // Check that the photo was added
        onView(withId(R.id.add_photo_grid)).check(matches(hasChildCount(2)));
        onView(withId(R.id.add_photo_grid)).check(matches(hasDescendant(allOf(withId(R.id.gallery_image_view), isDisplayed()))));
    }
}