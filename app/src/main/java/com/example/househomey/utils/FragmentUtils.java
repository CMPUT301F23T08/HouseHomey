package com.example.househomey.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * This is a utility class for fragment page navigation within an Android application.
 *
 * @author Owen Cooke, Matthew Neufeld
 */
public class FragmentUtils {

    /**
     * Navigates to a specified fragment page by replacing the fragment
     * within the provided AppCompatActivity's fragment container.
     *
     * @param context The AppCompatActivity instance used for accessing the FragmentManager.
     * @param page    The Fragment to navigate to and replace the current fragment with.
     */
    public static void navigateToFragmentPage(Context context, Fragment page) {
        navigateToFragmentPage(context, page, R.id.fragmentContainer);
    }

    /**
     * Navigates to a specified fragment page by replacing the fragment
     * within the provided AppCompatActivity's fragment container.
     *
     * @param context The AppCompatActivity instance used for accessing the FragmentManager.
     * @param page    The Fragment to navigate to and replace the current fragment with.
     * @param fragmentContainer The parent container of the old fragment which will contain the new one.
     */
    public static void navigateToFragmentPage(Context context, Fragment page, @IdRes int fragmentContainer) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentContainer, page);
        transaction.addToBackStack(page.toString());
        transaction.commit();
    }

    /**
     * Specialized method for navigating back to the HomeFragment
     * while also setting the selected item in the BottomNavigationView
     *
     * @param context The AppCompatActivity context from which the navigation is initiated.
     */
    public static void navigateHomeWithIndicator(Context context) {
        BottomNavigationView bottomNavigationView = ((AppCompatActivity) context).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    /**
     * Navigates the fragment manager back to the previous fragment if there is a fragment in the back stack.
     *
     * @param context The AppCompatActivity context where the navigation is called.
     */
    public static void goBack(Context context) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    /**
     * Enables date picking for the calendar
     *
     * @return datePicker
     */
    public static MaterialDatePicker<Long> createDatePicker() {
        // Create constraint to restrict dates to past/present
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis());
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        return MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
    }

    /**
     * Formats given date as a string in "yyyy-MM-dd" format.
     *
     * @param date date to be formatted
     * @return formatted date
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    /**
     * Makes a chip and adds it to a chip group
     *
     * @param label               label that will go on the chip
     * @param closeIconVisibility boolean: if true, closeIcon is visible, if false, not visible
     * @param chipGroup           group of chips the chip will be added to
     * @param context             the given context
     * @param backgroundColour    background colour of the chip
     * @param strokeColour        stroke colour of the chip
     * @param textColour          text colour of the chip
     * @return a new chip
     */
    public static Chip makeChip(String label, Boolean closeIconVisibility, ChipGroup chipGroup, Context context, int backgroundColour, int strokeColour, int textColour, boolean checkable) {
        Chip chip = new Chip(context);
        chip.setText(label);
        chip.setCloseIconVisible(closeIconVisibility);
        chip.setChipBackgroundColorResource(backgroundColour);
        chip.setChipStrokeColorResource(strokeColour);
        chip.setTextColor(ContextCompat.getColor(context, textColour));
        chip.setCheckable(checkable);
        chipGroup.addView(chip);
        return chip;
    }

    /**
     * uses MakeChip to make a new chip
     *
     * @param label               label that will go on the chip
     * @param closeIconVisibility boolean: if true, closeIcon is visible, if false, not visible
     * @param chipGroup           group of chips the chip will be added to
     * @param context             the given context
     * @param backgroundColour    background colour of the chip
     * @param strokeColour        stroke colour of the chip
     * @param textColour          text colour of the chip
     * @return a chip provided by makeChip
     */
    public static Chip makeChip(String label, Boolean closeIconVisibility, ChipGroup chipGroup, Context context, int backgroundColour, int strokeColour, int textColour) {
        return makeChip(label, closeIconVisibility, chipGroup, context, backgroundColour, strokeColour, textColour, false);
    }

    /**
     * Determines whether a string is a valid UUID or not.
     * Used for Cloud Storage image UUIDs.
     *
     * @param str a string
     */
    public static boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Deletes photos from Firebase Cloud Storage based on the user and provided list of photo IDs.
     *
     * @param context  The context associated with the Firebase storage operations.
     * @param photoIds  A list of photo IDs (UUIDs) representing the image filenames to be deleted.
     */
    public static void deletePhotosFromCloud(Context context, List<String> photoIds) {
        for (String imageId : photoIds) {
            StorageReference imageRef = ((MainActivity) context).getImageRef(imageId);
            imageRef.delete()
                    .addOnSuccessListener(taskSnapshot -> Log.d("IMAGE_DELETE", "Successfully removed image from: " + imageRef))
                    .addOnFailureListener(e -> Log.e("IMAGE_DELETE", "Failed to delete image from Cloud Storage: " + e));
        }
    }

    /**
     * Navigates to fragment without adding it to back stack, this is for moving between the 3
     * primary fragments on the bottom navbar
     * @param fragmentToShow page to navigate to
     * @param context The AppCompatActivity context where the navigation is called
     */
    public static void navigateViaBottomNavBar(Fragment fragmentToShow, Context context) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount()!=0) {
            fragmentManager.popBackStack(fragmentToShow.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmentToShow)
                .commit();
    }

    /**
     * Takes an unformatted string and converts it to initial case
     *
     * @param label the unformatted label
     * @return the formatted label
     */
    public static String initialCase(String label) {
        label = label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase();
        return label;
    }
}

