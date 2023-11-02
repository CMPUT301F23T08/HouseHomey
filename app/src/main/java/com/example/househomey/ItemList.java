package com.example.househomey;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemList {

    private ArrayList<Item> items = new ArrayList<>();
    private String username;

    private CollectionReference itemsRef;
    private FirestoreUpdateListener listener;

    public ItemList(FirestoreUpdateListener listener, CollectionReference itemsRef) {
        this.listener = listener;
        this.itemsRef = itemsRef;
        this.username = username;
        initItems();
    }

    private void initItems() {
        itemsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                items.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    Map<String, Object> data = new HashMap<>();
                    data.putAll(doc.getData());
                    items.add(new Item(doc.getId(), data));
                    listener.notifyDataSetChanged();
                }
            }
        });
    }

    public void addItem(Item item) {
        itemsRef.add(item.data());
    }

    public ArrayList<Item> getItems() {
        return items;
    }

}
