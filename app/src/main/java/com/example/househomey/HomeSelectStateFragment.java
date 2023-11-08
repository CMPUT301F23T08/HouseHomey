package com.example.househomey;

import com.google.firebase.firestore.CollectionReference;

public class HomeSelectStateFragment extends HomeFragment {


    /**
     * This constructs a new HomeFragment with the appropriate list of items
     *
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeSelectStateFragment(CollectionReference itemRef) {
        super(itemRef);
    }
}
