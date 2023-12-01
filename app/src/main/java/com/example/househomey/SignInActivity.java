package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateToFragmentPage;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * SignInActivity of the application, handles setting up the sign in page
 * @author Antonio Lech Martin-Ozimek
 */
public class SignInActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides a Bundle
     * containing the activity's previously frozen state, if there was one.
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
