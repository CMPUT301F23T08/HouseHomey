package com.example.househomey.testUtils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;

import com.example.househomey.MainActivity;
import com.example.househomey.item.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
 * A JUnit TestRule that provides the ability to create and delete a unique test user document for each test.
 * NOTE: this Rule will only create a unique user for the test IF
 *      the test method contains the `methodMatcher` as part of its name.
 *
 * @author Owen Cooke
 */
public class DatabaseSetupRule<T extends Activity> implements TestRule {
    private final String methodMatcher = "WithNewUser";
    private final Class<T> activityClass;
    private final int dbTimeoutInSeconds = 30;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private DocumentReference userDoc;
    private boolean isDatabaseTest;
    private T activity;
    private final String password = "123456";

    public DatabaseSetupRule(Class<T> activityClass) {
        this.activityClass = activityClass;
    }

    /*
     * Sets up the activity by launching it, providing user data via an intent.
     * The user data includes the user document's ID, or null if the test does not require a unique user.
     * ESPRESSO_GENERAL_USER exists in Firebase for testing general, non-DB changing tests
     * ex) proper page navigation, field validation, etc.
     */
    public T setupActivity() {
        ActivityScenario.launch(activityClass).onActivity(activity -> {
            this.activity = activity;
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });
        return activity;
    }

    /*
     * Adds a mock item to the Firestore database using the provided item details.
     * NOTE: this method is IGNORED if the test does not require/create a unique user
     *
     * @param itemDetails A Map containing the details of the mock Item to be added.
     * @throws RuntimeException if the mock data cannot create a valid Item, if adding the mock item to Firestore
     * fails, or if there is a timeout waiting for the operation to complete.
     */
    public void addTestItem(Map<String, Object> itemDetails) {
        if (userDoc != null) {
            // Ensure that mock data can be used to create a valid Item
            Item item;
            try {
                item = new Item("", itemDetails, userDoc.collection("tag"), item1 -> {
                });
            } catch (NullPointerException e) {
                throw new RuntimeException("Mock data cannot create a valid Item: " + e.getMessage());
            }
            CountDownLatch latch = new CountDownLatch(1);
            // Create new item in DB
            userDoc.collection("item").add(item.getData()).addOnCompleteListener(task -> latch.countDown()).addOnFailureListener(e -> {
                latch.countDown();
                throw new RuntimeException("Adding mock item to Firestore failed with: " + e.getMessage());
            });
            // Wait for item creation to finish
            try {
                if (!latch.await(dbTimeoutInSeconds, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timeout waiting for test user creation.");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Timeout waiting for test user creation.");
            }
        }
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
                } else {
                    loginToGeneralTestUser();
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

    public void loginToGeneralTestUser() {
        CountDownLatch latch = new CountDownLatch(1);
        auth.signInWithEmailAndPassword(getEmail(), password)
                .addOnSuccessListener(authResult -> {
                    Log.d("ESP-TEST", "ESPRESSO_GENERAL_USER logged in successfully");
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("ESP-TEST", "Login failed for ESPRESSO_GENERAL_USER: " + e.getMessage());
                    latch.countDown();
                });

        // Wait for the login to finish
        try {
            if (!latch.await(dbTimeoutInSeconds, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for user login.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Timeout waiting for user login.");
        }
    }


    private void createTestUser() throws Exception {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(1);

        // Create a unique Firebase Auth test user and document
        String email = getEmail();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(aVoid -> {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(email).build();
                    auth.getCurrentUser().updateProfile(profileChangeRequest);
                    db.collection("user").
                            document(email).set(new HashMap<>()).addOnSuccessListener(a -> {
                                userDoc = db.collection("user").document(email);
                                latch.countDown();
                            });
                }).addOnFailureListener(e -> {
                    throw new RuntimeException("Could not create Firestore document for the test user: " + e.getMessage());
                })).addOnFailureListener(e -> {
                    throw new RuntimeException("Could not create Firebase Auth user for the test: " + e.getMessage());
                });

        // Wait for user creation and Firestore document creation to finish
        if (!latch.await(dbTimeoutInSeconds, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for test user creation.");
        }
    }

    private void deleteTestUser() {
        if (userDoc != null) {
            // Need to delete all nested collections before user doc can be deleted
            deleteNestedDocuments(userDoc.collection("item"));
            deleteNestedDocuments(userDoc.collection("tag"));

            // Delete Firestore document
            userDoc.delete().addOnSuccessListener(aVoid -> Log.d("ESP-TEST", "Unique test user document deleted successfully"))
                    .addOnFailureListener(e -> Log.e("ESP-TEST", "Could not delete the unique test user document: " + e.getMessage()));

            // Delete Firebase Auth test user
            auth.getCurrentUser().delete().addOnSuccessListener(aVoid -> Log.d("ESP-TEST", "Firebase Auth user deleted successfully"))
                    .addOnFailureListener(e -> Log.e("ESP-TEST", "Could not delete Firebase Auth user: " + e.getMessage()));
        }
    }

    private void deleteNestedDocuments(CollectionReference collection) {
        collection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                DocumentReference docRef = document.getReference();
                docRef.delete().addOnSuccessListener(aVoid -> Log.d("ESP-TEST", "Nested document deleted successfully")).addOnFailureListener(e -> Log.e("ESP-TEST", "Could not delete nested document: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> Log.e("ESP-TEST", "Could not retrieve nested documents: " + e.getMessage()));
    }

    private String getEmail() {
        if (isDatabaseTest) return UUID.randomUUID() + "@example.com";
        return "espresso_general_user@example.com";
    }
}

