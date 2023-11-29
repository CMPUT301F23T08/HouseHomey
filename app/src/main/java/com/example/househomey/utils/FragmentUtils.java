package com.example.househomey.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.househomey.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, page);
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
}

