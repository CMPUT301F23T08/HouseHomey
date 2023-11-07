package com.example.househomey.testUtils;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.househomey.MainActivity;

import org.junit.Before;
import org.junit.Rule;

public abstract class MainActivityTestSetup {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        String testUser = "intent_test_user";
        Bundle userData = new Bundle();
        userData.putString("username", testUser);

        // Pass the userData Bundle to MainActivity
        activityRule.getScenario().onActivity(activity -> {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("userData", userData);
            activity.startActivity(intent);
        });
    }
}
