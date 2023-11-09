package com.example.househomey.testUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;

import com.example.househomey.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A JUnit TestRule that provides the ability to create and delete a unique test user document for each test.
 * NOTE: this Rule will only create a unique user for the test IF
 *      the test method contains the `methodMatcher` as part of its name.
 *
 * @author Owen Cooke
 */
public class DatabaseSetupRule<T extends Activity> implements TestRule {
    private final String methodMatcher = "WithNewUser";
    private final Class<T> activityClass;
    private DocumentReference userDoc;
    private boolean isDatabaseTest;

    public DatabaseSetupRule(Class<T> activityClass) {
        this.activityClass = activityClass;
    }

    /**
     * Sets up the activity by launching it, providing user data via an intent.
     * The user data includes the user document's ID, or null if the test does not require a unique user.
     * TODO: we can replace the "null" test user with an actual username, and prepopulate that user
     *      in Firebase for testing non-DB updates (such as list formatting, item filtering, sorting, etc.)
     */
    public void setupActivity() {
        Bundle userData = new Bundle();
        userData.putString("username", isDatabaseTest ? userDoc.getId() : "null");
        ActivityScenario.launch(activityClass).onActivity(activity -> {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("userData", userData);
            activity.startActivity(intent);
        });
    }

    @Override
    public Statement apply(@NonNull Statement base, @NonNull Description description) {
        // Check if the test method name indicates that new user Firestore setup is needed
        String methodName = description.getMethodName();
        isDatabaseTest = methodName.contains(methodMatcher);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (isDatabaseTest) {
                    createTestUser();
                }
                try {
                    // Run the actual @Test method body
                    base.evaluate();
                } finally {
                    if (isDatabaseTest) {
                        deleteTestUser();
                    }
                }
            }
        };
    }

    private void createTestUser() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        db.collection("user").add(new HashMap<String, Object>()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDoc = task.getResult();
                latch.countDown();
            } else {
                throw new RuntimeException("Could not generate a unique test user.");
            }
        });
        // Wait for user creation to finish
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for test user creation.");
        }
    }

    public void deleteTestUser() throws Exception {
        if (userDoc != null) {
            CountDownLatch latch = new CountDownLatch(1);
            // Need to delete all nested collections before user doc can be deleted
            deleteNestedDocuments(userDoc.collection("item"));
            userDoc.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ESP-TEST", "Unique test user document deleted successfully");
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ESP-TEST", "Could not delete the unique test user: " + e.getMessage());
                        latch.countDown();
                    });
            // Wait for all deletions to finish
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for test user deletion.");
            }
        }
    }

    private void deleteNestedDocuments(CollectionReference collection) {
        collection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                DocumentReference docRef = document.getReference();
                docRef.delete()
                        .addOnSuccessListener(aVoid -> Log.d("ESP-TEST", "Nested document deleted successfully"))
                        .addOnFailureListener(e -> Log.e("ESP-TEST", "Could not delete nested document: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> Log.e("ESP-TEST", "Could not retrieve nested documents: " + e.getMessage()));
    }
}

