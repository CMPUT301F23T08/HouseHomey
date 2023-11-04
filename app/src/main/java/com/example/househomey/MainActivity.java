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

public class MainActivity extends AppCompatActivity {
    // Define constants for filter items
    private User user;
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

    private void navigateToFragment(Fragment fragment) {
        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the addItemFragment
        transaction.replace(R.id.fragmentContainer, fragment);

        // Commit the transaction
        transaction.addToBackStack(null); // Optional, to add the transaction to the back stack
        transaction.commit();
    }

//    public void showFilterDropdown(View view) {
//        PopupMenu filterDropdown = new PopupMenu(this, view);
//        filterDropdown.setOnMenuItemClickListener(this);
//        filterDropdown.inflate(R.menu.filter);
//        filterDropdown.show();
//    }
//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        if (item.getItemId() == R.id.filter1) {
//            Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (item.getItemId() == R.id.filter2) {
//            Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
//            return false;
//        }
//    }
}