package com.example.househomey;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private EditText confirmPasswordEdittext;
    private String confirmedPassword;
    private CollectionReference collRef;
    private Button signupButton;
    // Define a regex pattern for lowercase alphanumeric with underscores or periods
    private final String regex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private FirebaseAuth auth;
    private String username;
    private String password;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_signup, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        usernameEdittext = rootView.findViewById(R.id.signup_username);
        passwordEdittext = rootView.findViewById(R.id.signup_password);
        confirmPasswordEdittext = rootView.findViewById(R.id.signup_confirm_password);
        signupButton = rootView.findViewById(R.id.signup_button);
        TextView signinRedirectMessage = rootView.findViewById(R.id.signin_redirect_message);
        TextView signinRedirect = rootView.findViewById(R.id.signin_redirect);

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
            boolean filled = confirmPassword();
            if (filled) {
                auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Map<String, Object> fields = new HashMap<>();
                        fields.put("username", user.getEmail());
                        collRef.document(user.getEmail()).set(fields).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // Replace YourSignUpFragment with the actual class name of your sign-up fragment
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.fragmentContainerSignIn, new SignInFragment());
                                transaction.addToBackStack("signup");
                                transaction.commit();
                            } else {
                                usernameEdittext.setError(task1.getException().getMessage());
                                passwordEdittext.setError(task1.getException().getMessage());
                            }
                        });
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        //If email already registered.
                        usernameEdittext.setError(task.getException().getMessage());

                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        //if password not 'stronger'
                        passwordEdittext.setError(task.getException().getMessage());
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
                    usernameEdittext.setError("must be an email");
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
