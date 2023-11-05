package com.example.househomey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.househomey.R;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * MainActivity of the application, handles setting up the bottom nav fragment and the user
 * @author Owen Cooke, Lukas Bonkowski
 */
public class MainActivity extends AppCompatActivity {
    // Define constants for filter items
    private User user;

    /**
     * Method to run on creation of the activity. Handles user setup and creates the bottom
     * nav fragment
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create or login a user, for now just assume...
        user = new User("john_doe");

        // Init home fragment
        navigateToFragment(new HomeFragment(user.getItemRef()));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.action_home) {
                // Go to Home page
                fragment = new HomeFragment(user.getItemRef());
            } else if (id == R.id.action_add) {
                // Go to Add Item page
                fragment = new AddItemFragment(user.getItemRef());
            } else {
                // TODO: Go to Profile Page
                fragment = new HomeFragment(user.getItemRef());
            }
            navigateToFragment(fragment);
            return true;
        });
    }

    /**
     * Changes the current fragment to the passed fragment
     * @param fragment a Fragment to navigate to and replace the current fragment with
     */
    private void navigateToFragment(Fragment fragment) {
        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new Fragment
        transaction.replace(R.id.fragmentContainer, fragment);

        // Commit the transaction
        transaction.addToBackStack(null); // Optional, to add the transaction to the back stack
        transaction.commit();
    }
}