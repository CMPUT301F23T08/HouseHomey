package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.app.Activity;
import android.content.Intent;
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {
    private User user;
    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private CollectionReference userRef;
    private DocumentReference docRef;
    private Button loginButton;
    // Define a regex pattern for lowercase alphanumeric with underscores or periods
    private String regex = "^[a-z0-9_.]+$";
    private boolean matches;
    private String username;
    private String password;
    private TextView signinRedirectMessage;
    private TextView signinRedirect;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_signin, container, false);

        usernameEdittext = rootView.findViewById(R.id.signin_username);
        passwordEdittext = rootView.findViewById(R.id.signin_password);
        loginButton = rootView.findViewById(R.id.signin_button);
        signinRedirectMessage = rootView.findViewById(R.id.signin_redirect_message);
        signinRedirect = rootView.findViewById(R.id.signin_redirect);

        loginButton.setText("Login");
        signinRedirectMessage.setText("Don't have an account?");
        signinRedirect.setText(R.string.sign_up_string);

        loginButton.setOnClickListener(v -> {
            loginButton.setBackgroundResource(R.drawable.signin_button_clicked);
            loginButton.setTextColor(getResources().getColor(R.color.creme, rootView.getContext().getTheme()));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loginButton.setBackgroundResource(R.drawable.signin_button_unclicked);
                loginButton.setTextColor(getResources().getColor(R.color.brown, rootView.getContext().getTheme()));
            }, 150);

            username = usernameEdittext.getText().toString().trim();
            password = passwordEdittext.getText().toString().trim();
            usernameEdittext.setError(null);
            passwordEdittext.setError(null);

            userRef = FirebaseFirestore.getInstance().collection("user");

            if (!username.isEmpty() & !password.isEmpty()) {
                docRef = userRef.document(username);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String db_password = (String) document.get("password");
                            if (Objects.equals(db_password, password)) {
                                Bundle userData = new Bundle();
                                userData.putString("username", username);
                                Activity activity = getActivity();
                                if (activity != null) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.putExtra("userData", userData);
                                    activity.startActivity(intent);
                                }
                            } else {
                                usernameEdittext.setError("username or password not recognized");
                                passwordEdittext.setError("username or password not recognized");
                            }
                        } else {
                            usernameEdittext.setError("username or password not recognized");
                            passwordEdittext.setError("username or password not recognized");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            } else {
                usernameEdittext.setError("username or password empty");
                passwordEdittext.setError("username or password empty");
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
                String password = editable.toString().trim();
                if (password.isEmpty()) {
                    passwordEdittext.setError("password cannot be empty");
                }
            }
        });

        signinRedirect.setOnClickListener(v -> {
            // Replace YourSignUpFragment with the actual class name of your sign-up fragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainerSignIn, new SignUpFragment());
            transaction.addToBackStack("signup");
            transaction.commit();
        });

        return rootView;
    }
}
