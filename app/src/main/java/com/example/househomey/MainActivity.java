package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static com.example.househomey.utils.FragmentUtils.navigateViaBottomNavBar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.househomey.form.AddItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;


/**
 * MainActivity of the application, handles setting up the bottom nav fragment and the user
 * @author Owen Cooke, Lukas Bonkowski
 */
public class MainActivity extends AppCompatActivity {
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

        // Handle the test user data
        Bundle userData = getIntent().getBundleExtra("userData");
        if (userData != null) {
            String username = userData.getString("username");
            user = new User(username);
        } else {
            // create or login a user, for now just assume...
            user = new User("john_doe");
        }

        // Init primary fragments and show home fragment
        HomeFragment homeFragment = new HomeFragment();
        AddItemFragment addFragment = new AddItemFragment();
        HomeFragment userFragment = new HomeFragment(); // TODO: initalize userProfilePage properly
        getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, homeFragment,homeFragment.toString())
                        .add(R.id.fragmentContainer, addFragment,addFragment.toString())
                        .add(R.id.fragmentContainer, userFragment,userFragment.toString())
                        .replace(R.id.fragmentContainer,homeFragment)
                        .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment;
            if (id == R.id.action_home) {
                // Go to Home page
                navigateViaBottomNavBar(homeFragment,this);
            } else if (id == R.id.action_add) {
                // Go to Add Item page
                navigateViaBottomNavBar(addFragment,this);
            } else {
                // TODO: Go to Profile Page
                navigateViaBottomNavBar(userFragment,this);
            }
            return true;
        });
    }

    /**
     * Retrieves the Firestore CollectionReference for items associated with the current user.
     *
     * @return A CollectionReference for the current user's items.
     */
    public CollectionReference getItemRef() { return user.getItemRef(); }

    /**
     * Retrieves the Firestore CollectionReference for items associated with a tag.
     *
     * @return A CollectionReference for the current user's items.
     */
    public CollectionReference getTagRef() { return user.getTagRef(); }

    /**
     * Retrieves a StorageReference to an image in Cloud Storage based on
     * the current user and the provided image ID.
     *
     * @param imageId The unique identifier of the image.
     * @return A StorageReference pointing to the specified image in Cloud Storage.
     */
    public StorageReference getImageRef(String imageId) {
        return FirebaseStorage.getInstance()
                .getReference("images/")
                .child(user.getUsername())
                .child(imageId);
    }

}