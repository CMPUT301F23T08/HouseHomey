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

    /**
     * Create a new item list for a given item collection
     *
     * @param listener The listener to update with changes to the item collection
     * @param itemsRef The item collection
     */
    public ItemList(FirestoreUpdateListener listener, CollectionReference itemsRef) {
        this.listener = listener;
        this.itemsRef = itemsRef;
        initItems();
    }

    /**
     * Setup the itemsRef CollectionReference to listen for database changes
     */
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

    /**
     * Add an item to the user's item list via firestore
     *
     * @param item The item to add
     */
    public void addItem(Item item) {
        itemsRef.add(item.data());
    }

    /**
     * Delete an item from the user's list via firestore
     * @param item The item to delete
     */
    public void deleteItem(Item item) {
        itemsRef.document(item.getId()).delete();
    }

    /**
     * @return An ArrayList of items in the Item List
     */
    public ArrayList<Item> getItems() {
        return items;
    }

}
