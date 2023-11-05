package com.example.househomey;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class represents a user and gets references to the appropriate information from firestore
 * @author Lukas Bonkowski, Owen Cooke
 */
public class User {
    private String username;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private CollectionReference itemRef;

    /**
     * This constructs a new user getting references to their firestore information
     * @param username The unique username for this user to reference in firestore to find their
     *                 item collection
     */
    public User(String username) {
        db = FirebaseFirestore.getInstance();
        this.username = username;
        userRef = db.collection("user").document(username);
        itemRef = userRef.collection("item");
    }

    /**
     * Getter for itemRef
     * @return A reference to the user's firestore item collection
     */
    public CollectionReference getItemRef() {
        return itemRef;
    }
}
