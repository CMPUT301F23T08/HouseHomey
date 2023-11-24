package com.example.househomey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;

public class UserProfileFragment extends Fragment {

    private String username;

    TextView username_text_view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //Populate the item list from bundle
        Bundle args = getArguments();

        this.username = args.getString("username");

        username_text_view = rootView.findViewById(R.id.user_profile_username);
        username_text_view.setText(this.username);

        return rootView;
    }
}
