package com.example.househomey.form;

import static com.example.househomey.utils.FragmentUtils.navigateHomeWithIndicator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.househomey.Item;
import com.example.househomey.R;

/**
 * This fragment is responsible for creating and loading to the database a new item
 *
 * @author Owen Cooke
 */
public class AddItemFragment extends ItemFormFragment {
    /**
     * This creates the view to add an item to a user's inventory and set's the button listeners
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The fragment_add_item view with the correct listeners added
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        // Add listeners for buttons and text validation
        initDatePicker(rootView);
        initTextValidators(rootView);
        rootView.findViewById(R.id.add_item_confirm_button).setOnClickListener(v -> addItem());
        rootView.findViewById(R.id.add_item_back_button).setOnClickListener(v -> navigateHomeWithIndicator(getContext()));
        return rootView;
    }

    /**
     * Adds the user input data to a new item in a user's Firestore item collection
     */
    private void addItem() {
        Item newItem = validateItem("");
        if (newItem == null) return;

        // Create new item document in Firestore
        itemRef.add(newItem.getData()).addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Successfully created new item with id:" + documentReference.getId());
                    navigateHomeWithIndicator(getContext());
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Failed to create new item");
                    getView().findViewById(R.id.add_item_error_msg).setVisibility(View.VISIBLE);
                });
    }
}
