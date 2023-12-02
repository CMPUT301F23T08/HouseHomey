package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    private EditText usernameEdittext;
    private EditText emailEdittext;
    private EditText passwordEdittext;
    private EditText confirmPasswordEdittext;
    private String confirmedPassword;
    private CollectionReference collRef;
    private Button signupButton;
    // Define a regex pattern for lowercase alphanumeric with underscores or periods
    private final String emailRegex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final String usernameRegex = "^[a-zA-Z0-9_.]+$";
    private FirebaseAuth auth;
    private String username;
    private String email;
    private String password;

    /**
     * Overrides the default implementation to inflate the layout for the SignUpFragment,
     * initialize Firebase Authentication, and set up UI components such as username, email,
     * password, and confirmation fields, and the registration button. Handles user input for
     * registration, validates user inputs, creates a new user account using Firebase
     * Authentication, updates the user profile, and stores user data in Firestore. Navigates
     * to the SignInFragment when the redirection link is clicked.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in
     *                           the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to. The fragment should not add the view itself, but
     *                           this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_signup, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        usernameEdittext = rootView.findViewById(R.id.signup_username);
        emailEdittext = rootView.findViewById(R.id.signup_email);
        passwordEdittext = rootView.findViewById(R.id.signup_password);
        confirmPasswordEdittext = rootView.findViewById(R.id.signup_confirm_password);
        signupButton = rootView.findViewById(R.id.signup_button);

        TextView signInRedirectMessage = rootView.findViewById(R.id.signin_redirect_message);
        TextView signInRedirect = rootView.findViewById(R.id.signin_redirect);

        signupButton.setText("Register");
        signInRedirectMessage.setText("Already have an account?");
        signInRedirect.setText(R.string.sign_in_string);

        signupButton.setOnClickListener(v -> {
            signupButton.setBackgroundResource(R.drawable.signin_button_clicked);
            signupButton.setTextColor(getResources().getColor(R.color.creme, rootView.getContext().getTheme()));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                signupButton.setBackgroundResource(R.drawable.signin_button_unclicked);
                signupButton.setTextColor(getResources().getColor(R.color.brown, rootView.getContext().getTheme()));
            }, 150);

            collRef = FirebaseFirestore.getInstance().collection("user");
            if (confirmPassword()) {
                collRef.document(username).get().addOnSuccessListener(task -> {
                    if (task.getData() == null) {
                        UserProfileChangeRequest profileChangeRequest =
                                new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user == null) {
                                    emailEdittext.setError("database error");
                                    usernameEdittext.setError("database error");
                                } else {
                                    user.updateProfile(profileChangeRequest).addOnSuccessListener(task12 -> {
                                        Map<String, Object> fields = new HashMap<>();
                                        fields.put("email", user.getEmail());
                                        collRef.document(user.getDisplayName()).set(fields).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                navigateToFragmentPage(getActivity(), new SignInFragment(), R.id.fragmentContainerSignIn);
                                            } else {
                                                usernameEdittext.setError(task1.getException().getMessage());
                                                passwordEdittext.setError(task1.getException().getMessage());
                                            }
                                        });
                                    });
                                }
                            } else if (task1.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                //If email already registered.
                                emailEdittext.setError(task1.getException().getMessage());

                            } else if (task1.getException() instanceof FirebaseAuthWeakPasswordException) {
                                //if password not 'stronger'
                                passwordEdittext.setError(task1.getException().getMessage());
                            }
                        });
                    } else {
                        usernameEdittext.setError("username already exists");
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
               username = editable.toString().trim();

                // Use Pattern and Matcher to check if the username matches the pattern
                Pattern pattern = Pattern.compile(usernameRegex);
                Matcher matcher = pattern.matcher(username);

                if (username.isEmpty()) {
                    usernameEdittext.setError("username cannot be empty");
                } else if(!matcher.matches()) {
                    usernameEdittext.setError("only alphanumeric, underscore, or period");
                }
            }
        });

        emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = editable.toString().trim();
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email);

                if (email.isEmpty()) {
                    emailEdittext.setError("email cannot be empty");
                } else if (!matcher.matches()) {
                    emailEdittext.setError("not a valid email");
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

        signInRedirect.setOnClickListener(v -> navigateToFragmentPage(getActivity(), new SignInFragment(), R.id.fragmentContainerSignIn));
        return rootView;
    }

    /**
     * Validates the user's registration inputs, including username, email, password, and confirmation
     * password. Checks for non-empty and error-free inputs, ensuring that the confirmation password
     * matches the original password. Clears any previous error messages associated with the fields
     * upon successful validation.
     *
     * @return {@code true} if all registration inputs are valid; {@code false} otherwise.
     */
    private boolean confirmPassword() {
        if (!password.isEmpty() & !username.isEmpty() & !email.isEmpty()){
            if (passwordEdittext.getError() == null & usernameEdittext.getError() == null & emailEdittext.getError() == null) {
                if(confirmedPassword.equals(password) & confirmPasswordEdittext.getError() == null){
                    usernameEdittext.setError(null);
                    emailEdittext.setError(null);
                    passwordEdittext.setError(null);
                    confirmPasswordEdittext.setError(null);
                    return true;
                }
            }
        }
        return false;
    }
}
