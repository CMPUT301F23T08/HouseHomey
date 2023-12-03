package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static com.example.househomey.utils.FragmentUtils.navigateViaBottomNavBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.househomey.form.AddItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            user = new User(currentUser.getDisplayName());
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            this.startActivity(intent);
            this.finish();
        }


        // Init primary fragments and show home fragment
        HomeFragment homeFragment = new HomeFragment();
        AddItemFragment addFragment = new AddItemFragment();
        UserProfileFragment userFragment = new UserProfileFragment();
        getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, homeFragment,homeFragment.toString())
                        .add(R.id.fragmentContainer, addFragment,addFragment.toString())
                        .add(R.id.fragmentContainer, userFragment,userFragment.toString())
                        .replace(R.id.fragmentContainer,homeFragment)
                        .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_home) {
                // Go to Home page
                navigateViaBottomNavBar(homeFragment,this);
            } else if (id == R.id.action_add) {
                // Go to Add Item page
                navigateViaBottomNavBar(addFragment,this);
            } else {
                Bundle name = new Bundle();
                name.putString("username", user.getUsername());
                userFragment.setArguments(name);
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