package com.example.househomey;

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

import com.google.firebase.auth.FirebaseAuth;

public class UserProfileFragment extends Fragment {

    private String username;
    TextView usernameTextView;
    Button logoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //Populate the item list from bundle
        Bundle args = getArguments();

        this.username = args.getString("username");

        usernameTextView = rootView.findViewById(R.id.user_profile_username);
        logoutButton = rootView.findViewById(R.id.user_profile_logout_button);

        usernameTextView.setText(this.username);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        return rootView;
    }
}
