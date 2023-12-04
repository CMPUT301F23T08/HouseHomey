package com.example.househomey.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.househomey.R;
import com.example.househomey.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileFragment extends Fragment {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Called to create the user profile fragment's view hierarchy. Inflates the fragment layout from
     * the specified XML resource, populates the item list from the bundle arguments, and sets up UI
     * elements such as the username TextView and logout button.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in
     *                           the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be
     *                           attached to. The fragment should not add the view itself, but this can
     *                           be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The root view of the fragment's layout hierarchy.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Set the user's info
        ((TextView) rootView.findViewById(R.id.user_profile_username)).setText(auth.getCurrentUser().getDisplayName());
        ((TextView) rootView.findViewById(R.id.user_profile_email)).setText(auth.getCurrentUser().getEmail());

        final Button logoutButton = rootView.findViewById(R.id.user_profile_logout_button);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            logoutButton.setBackgroundResource(R.drawable.logout_button_clicked);
            logoutButton.setTextColor(getResources().getColor(R.color.creme, rootView.getContext().getTheme()));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                logoutButton.setBackgroundResource(R.drawable.logout_button_unclicked);
                logoutButton.setTextColor(getResources().getColor(R.color.black, rootView.getContext().getTheme()));
            }, 100);
            Activity activity = getActivity();
            if (activity != null) {
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
            }
        });

        return rootView;
    }
}
