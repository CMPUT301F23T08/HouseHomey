package com.example.househomey.signin;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.househomey.R;


/**
 * SignInActivity of the application, handles setting up the sign in page
 * @author Antonio Lech Martin-Ozimek
 */
public class SignInActivity extends AppCompatActivity {

    /**
     * This launches the SignInActivity which is the basis for both the SignInFragment
     * and the SignUpFragment. Once the user has signed in we exit this activity.
     *
     * @param savedInstanceState A Bundle containing the activity's previously frozen state, or null
     *                           if there was none.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        navigateToFragmentPage(this, new SignInFragment(), R.id.fragmentContainerSignIn);

    }
}
