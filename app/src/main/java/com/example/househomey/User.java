package com.example.househomey;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

/**
 * This class represents a user and gets references to the appropriate information from firestore
 * @author Lukas Bonkowski, Owen Cooke
 */
public class User {
    private final String username;
    private final CollectionReference itemRef;

    /**
     * This constructs a new user getting references to their firestore information
     * @param username The unique username for this user to reference in firestore to find their
     *                 item collection
     */
    public User(String username) {
        this.username = username;
        itemRef = FirebaseFirestore.getInstance().collection("user/" + username + "/item");
    }

    /**
     * Getter for itemRef
     * @return A reference to the user's firestore item collection
     */
    public CollectionReference getItemRef() {
        return itemRef;
    }

    /**
     * Getter for username
     * @return The user's unique username as a String
     */
    public String getUsername() {
        return username;
    }
}
