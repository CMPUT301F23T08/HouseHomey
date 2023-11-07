package com.example.househomey.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.househomey.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This is a utility class for fragment page navigation within an Android application.
 * @author Owen Cooke
 */
public class FragmentUtils {

    /**
     * Navigates to a specified fragment page by replacing the fragment
     * within the provided AppCompatActivity's fragment container.
     * @param context The AppCompatActivity instance used for accessing the FragmentManager.
     * @param page    The Fragment to navigate to and replace the current fragment with.
     */
    public static void navigateToFragmentPage(AppCompatActivity context, Fragment page) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, page);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Specialized method for navigating back to the HomeFragment
     * while also setting the selected item in the BottomNavigationView
     * @param context The AppCompatActivity context from which the navigation is initiated.
     */
    public static void navigateHomeWithIndicator(AppCompatActivity context) {
        BottomNavigationView bottomNavigationView = context.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}

