package com.example.househomey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements FirestoreUpdateListener {
    private FirebaseFirestore db;
    private ListView itemListView;
    private ArrayAdapter<Item> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        // create or login a user, for now just assume...
        User user = new User(this,"john_doe");

        itemListView = findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(this, user.getItemList().getItems());
        itemListView.setAdapter(itemAdapter);

        // Init home fragment
        navigateToFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.action_home) {
                // Go to Home page
                fragment = new HomeFragment();
            } else if (id == R.id.action_add) {
                // Go to Add Item page
                fragment = new AddItemFragment();
            } else {
                // FIXME: Go to Profile Page
                fragment = new HomeFragment();
            }
            navigateToFragment(fragment);
            return true;
        });
    }

    @Override
    public void notifyDataSetChanged() {
        itemAdapter.notifyDataSetChanged();
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
}