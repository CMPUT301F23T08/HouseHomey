package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * SignInFragment of the application, defines the sign in page
 * and links to the SignUpFragment. Deals with validating sign in
 * information from user.
 * @author Antonio Lech Martin-Ozimek
 */
public class SignInFragment extends Fragment {
    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private CollectionReference userRef;
    private DocumentReference docRef;
    private Button loginButton;
    // Define a regex pattern for lowercase alphanumeric with underscores or periods
    private final String regex = "^[a-zA-Z0-9_.]+$";
    private FirebaseAuth auth;
    private String username;
    private String password;

    /**
     * Overrides the default implementation to inflate the layout for the SignInFragment,
     * initialize Firebase Authentication, and set up UI components such as username and password
     * fields, login button, and redirection text views. Handles user input for login, validates
     * username and password, and initiates the login process using Firebase Authentication.
     * Navigates to the Sign Up fragment when the redirection link is clicked.
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
        View rootView = inflater.inflate(R.layout.fragment_user_signin, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        auth.signOut();

        usernameEdittext = rootView.findViewById(R.id.signin_username);
        passwordEdittext = rootView.findViewById(R.id.signin_password);
        loginButton = rootView.findViewById(R.id.signin_button);
        TextView signInRedirectMessage = rootView.findViewById(R.id.signin_redirect_message);
        TextView signInRedirect = rootView.findViewById(R.id.signin_redirect);

        loginButton.setText(R.string.signin_button_text);
        signInRedirectMessage.setText(R.string.signin_text);
        signInRedirect.setText(R.string.sign_up_string);

        loginButton.setOnClickListener(v -> {
            loginButton.setBackgroundResource(R.drawable.signin_button_clicked);
            loginButton.setTextColor(getResources().getColor(R.color.creme, rootView.getContext().getTheme()));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loginButton.setBackgroundResource(R.drawable.signin_button_unclicked);
                loginButton.setTextColor(getResources().getColor(R.color.brown, rootView.getContext().getTheme()));
            }, 150);

            usernameEdittext.setError(null);
            passwordEdittext.setError(null);

            userRef = FirebaseFirestore.getInstance().collection("user");

            if (!username.isEmpty() & !password.isEmpty()) {
                docRef = userRef.document(username);
                docRef.get().addOnSuccessListener(task -> {
                    Map<String, Object> document = task.getData();
                    if (document != null) {
                        String email = (String) document.get("email");
                        if (email == null) {
                            usernameEdittext.setError("no email associated with this account");
                        } else {
                            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(task1 -> {
                                FirebaseUser user = task1.getUser();
                                if (user == null) {
                                    usernameEdittext.setError("database error");
                                    passwordEdittext.setError("database error");
                                } else {
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        Intent intent = new Intent(activity, MainActivity.class);
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }
                                }
                            });
                        }
                    } else {
                        usernameEdittext.setError("username or password not recognized");
                        passwordEdittext.setError("username or password not recognized");
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
                username = editable.toString().trim();

                // Use Pattern and Matcher to check if the username matches the pattern
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(username);

                if (username.isEmpty()) {
                    usernameEdittext.setError("username cannot be empty");
                } else if(!matcher.matches()) {
                    usernameEdittext.setError("only alphanumeric, underscore, or period");
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

        signInRedirect.setOnClickListener(v -> navigateToFragmentPage(getActivity(), new SignUpFragment(), R.id.fragmentContainerSignIn));

        return rootView;
    }
}
