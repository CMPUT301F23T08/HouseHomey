package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.househomey.form.AddItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


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

        // Init home fragment
        navigateToFragmentPage(this, new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment;
            if (id == R.id.action_home) {
                // Go to Home page
                fragment = new HomeFragment();
            } else if (id == R.id.action_add) {
                // Go to Add Item page
                fragment = new AddItemFragment();
            } else {
                // TODO: Go to Profile Page
                Bundle name = new Bundle();
                name.putString("username", "john_doe");
                fragment = new UserProfileFragment();
                fragment.setArguments(name);
            }
            navigateToFragmentPage(this, fragment);
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