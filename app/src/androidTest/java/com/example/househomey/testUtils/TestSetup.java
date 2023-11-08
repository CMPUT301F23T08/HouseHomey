package com.example.househomey.testUtils;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.househomey.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.io.IOException;

public abstract class TestSetup {
    String testUser = "intent_test_user";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void dismissANRSystemDialog() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If the device is running in English Locale
        UiObject waitButton = device.findObject(new UiSelector().textContains("wait"));
        if (waitButton.exists()) {
            waitButton.click();
        }
    }

    @BeforeClass
    public static void dismissAppCrashSystemDialogIfShown() {
        try {
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                    .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS");
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }

    @Before
    public void deleteTestUserItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemRef = db.collection("user").document(testUser).collection("item");
        // Delete each document in the collection
        itemRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    document.getReference().delete();
                }
            } else {
                throw new RuntimeException(task.getException());
            }
        });
    }

    @Before
    public void loginTestUser() {
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
