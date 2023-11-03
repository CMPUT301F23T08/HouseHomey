package com.example.househomey;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private String username;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private CollectionReference itemRef;

    public User(String username) {
        db = FirebaseFirestore.getInstance();
        this.username = username;
        userRef = db.collection("user").document(username);
        itemRef = userRef.collection("item");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CollectionReference getItemRef() {
        return itemRef;
    }
}
