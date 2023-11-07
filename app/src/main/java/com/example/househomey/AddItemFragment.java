package com.example.househomey;

import static com.example.househomey.utils.FragmentUtils.navigateHomeWithIndicator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;

import java.util.HashMap;

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
        rootView.findViewById(R.id.add_item_back_button).setOnClickListener(v -> navigateHomeWithIndicator((AppCompatActivity) getContext()));
        return rootView;
    }

    /**
     * Adds the user input data to a new item in their Firestore collection
     */
    private void addItem() {
        // Check that required fields are filled before submitting
        boolean invalidDesc = isRequiredFieldEmpty(R.id.add_item_description_layout);
        boolean invalidDate = isRequiredFieldEmpty(R.id.add_item_date_layout);
        boolean invalidCost = isRequiredFieldEmpty(R.id.add_item_cost_layout);
        if (invalidDesc || invalidDate || invalidCost) return;

        // Create map with form data
        HashMap<String, Object> data = new HashMap<>();
        data.put("description", getInputText(R.id.add_item_description));
        data.put("acquisitionDate", new Timestamp(dateAcquired));
        data.put("cost", getInputText(R.id.add_item_cost));
        data.put("make", getInputText(R.id.add_item_make));
        data.put("model", getInputText(R.id.add_item_model));
        data.put("serialNumber", getInputText(R.id.add_item_serial_number));
        data.put("comment", getInputText(R.id.add_item_comment));

        // Ensure that form data can be used to create a valid Item
        Item newItem;
        try {
            newItem = new Item("", data);
        } catch (NullPointerException e) {
            return;
        }

        // Create new item document in Firestore
        itemRef.add(newItem.getData()).addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Successfully created new item with id:" + documentReference.getId());
                    navigateHomeWithIndicator((AppCompatActivity) getContext());
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Failed to create new item");
                    getView().findViewById(R.id.add_item_error_msg).setVisibility(View.VISIBLE);
                });
    }
}
