package com.example.househomey;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    private User user;
    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private EditText confirmPasswordEdittext;
    private String confirmedPassword;
    private CollectionReference collRef;
    private DocumentReference docRef;
    private Button signupButton;
    // Define a regex pattern for lowercase alphanumeric with underscores or periods
    private String regex = "^[a-z0-9_.]+$";
    private boolean matches;
    private String username;
    private String password;
    private TextView signinRedirectMessage;
    private TextView signinRedirect;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_signup, container, false);

        usernameEdittext = rootView.findViewById(R.id.signup_username);
        passwordEdittext = rootView.findViewById(R.id.signup_password);
        confirmPasswordEdittext = rootView.findViewById(R.id.signup_confirm_password);
        signupButton = rootView.findViewById(R.id.signup_button);
        signinRedirectMessage = rootView.findViewById(R.id.signin_redirect_message);
        signinRedirect = rootView.findViewById(R.id.signin_redirect);

        signupButton.setText("Register");
        signinRedirectMessage.setText("Already have an account?");
        signinRedirect.setText(R.string.sign_in_string);

        signupButton.setOnClickListener(v -> {
            signupButton.setBackgroundResource(R.drawable.signin_button_clicked);
            signupButton.setTextColor(getResources().getColor(R.color.creme, rootView.getContext().getTheme()));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                signupButton.setBackgroundResource(R.drawable.signin_button_unclicked);
                signupButton.setTextColor(getResources().getColor(R.color.brown, rootView.getContext().getTheme()));
            }, 150);

            collRef = FirebaseFirestore.getInstance().collection("user");

            if (confirmPassword()) {
                // Create a new document with a field
                Map<String, Object> data = new HashMap<>();
                data.put("password", confirmedPassword);
                collRef.document(username).set(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Replace YourSignUpFragment with the actual class name of your sign-up fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragmentContainerSignIn, new SignInFragment());
                        transaction.addToBackStack("signup");
                        transaction.commit();
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            }
        });

        usernameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String username = editable.toString().trim();

                // Use Pattern and Matcher to check if the username matches the pattern
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(username);

                if (username.isEmpty()) {
                    usernameEdittext.setError("username cannot be empty");
                } else if(!matcher.matches()) {
                    usernameEdittext.setError("character not allowed");
                    matches = false;
                }
                if (!usernameEdittext.isFocused()) {
                    usernameEdittext.setError(null);
                }
            }
        });

        passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString().trim();
                if (password.isEmpty()) {
                    passwordEdittext.setError("password cannot be empty");
                }
            }
        });

        confirmPasswordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmedPassword = editable.toString().trim();
                if(!confirmedPassword.equals(password)) {
                    confirmPasswordEdittext.setError("passwords do not match");
                } else if (confirmedPassword.isEmpty()) {
                    confirmPasswordEdittext.setError("cannot be empty");
                }
            }
        });

        signinRedirect.setOnClickListener(v -> {
            // Replace YourSignUpFragment with the actual class name of your sign-up fragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainerSignIn, new SignInFragment());
            transaction.addToBackStack("signup");
            transaction.commit();
        });
        return rootView;
    }
    private boolean confirmPassword() {
        username = usernameEdittext.getText().toString().trim();
        password = passwordEdittext.getText().toString().trim();
        confirmedPassword = confirmPasswordEdittext.getText().toString().trim();
        if (!password.isEmpty() & !username.isEmpty()){
            if (passwordEdittext.getError() == null & usernameEdittext.getError() == null) {
                if(confirmedPassword.equals(password) & confirmPasswordEdittext.getError() == null){
                    usernameEdittext.setError(null);
                    passwordEdittext.setError(null);
                    confirmPasswordEdittext.setError(null);
                    return true;
                }
            }
        }
        return false;
    }
}
