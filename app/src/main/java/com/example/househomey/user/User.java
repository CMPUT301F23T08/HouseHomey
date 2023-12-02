package com.example.househomey.user;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class represents a user and gets references to the appropriate information from firestore
 * @author Lukas Bonkowski, Owen Cooke
 */
public class User {
    private final String username;
    private final CollectionReference itemRef;
    private final CollectionReference tagRef;

    /**
     * This constructs a new user getting references to their firestore information
     * @param username The unique username for this user to reference in firestore to find their
     *                 item collection
     */
    public User(String username) {
        this.username = username;
        itemRef = FirebaseFirestore.getInstance().collection("user/" + username + "/item");
        tagRef = FirebaseFirestore.getInstance().collection("user/" + username + "/tag");
    }

    /**
     * Getter for itemRef
     * @return A reference to the user's firestore item collection
     */
    public CollectionReference getItemRef() {
        return itemRef;
    }

    /**
     * Getter for tagRef
     * @return A reference to the user's firestore item collection
     */
    public CollectionReference getTagRef() { return tagRef; }

    /**
     * Getter for username
     * @return The user's unique username as a String
     */
    public String getUsername() {
        return username;
    }
}
