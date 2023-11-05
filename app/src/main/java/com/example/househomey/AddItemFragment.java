package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.Timestamp;


import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 *  This fragment is responsible for creating and loading to the database a new item
 * @author Owen Cooke
 */
public class AddItemFragment extends Fragment {
    private Date dateAcquired;
    private TextInputEditText dateTextView;
    private final CollectionReference itemRef;

    /**
     * Constructs a new AddItemFragment with a firestore reference
     * @param itemRef A reference to a firestore collection of items
     */
    public AddItemFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

    /**
     * This creates the view to add an item to a user's inventory and set's the button listeners
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
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Show DatePicker when date field selected
        dateTextView = rootView.findViewById(R.id.add_item_date);
        dateTextView.setOnClickListener(v -> showDatePicker());

        // Add listener for confirm and back buttons
        rootView.findViewById(R.id.add_item_confirm_button).setOnClickListener(v -> addItem());
        rootView.findViewById(R.id.add_item_back_button).setOnClickListener(v -> returnToHomeScreen());

        return rootView;
    }

    /**
     * This shows a datePicker for the user to select the date of the item acquisition
     */
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateAcquired = new Date(selection);
            dateTextView.setText(datePicker.getHeaderText());
        });

        datePicker.show(getParentFragmentManager(), "Date Picker");
    }

    /**
     * Adds the user input data to the firestore database
     */
    private void addItem() {
        // TODO: add input validation

        // Create map with form data
        HashMap<String, Object> data = new HashMap<>();
        data.put("description", getInputText(R.id.add_item_description));
        data.put("acquisitionDate", new Timestamp(dateAcquired));
        data.put("cost", getInputText(R.id.add_item_cost));
        data.put("make", getInputText(R.id.add_item_make));
        data.put("model", getInputText(R.id.add_item_model));
        data.put("serialNumber", getInputText(R.id.add_item_serial_number));
        data.put("comment", getInputText(R.id.add_item_comment));

        // Create new item document in Firestore
        itemRef.add(data).addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Successfully created new item with id:" + documentReference.getId());
                    returnToHomeScreen();
                })
                .addOnFailureListener(e -> {
                    // TODO: Handle the failure to add the document
                    Log.d("Firestore", "Failed to create new item");
                });
    }

    /**
     * Gets the user input as a string from a given TextInputEditText
     * @param id Id of the TextInputEditText object
     * @return The user input String
     * @throws NullPointerException if the input text is null
     */
    private String getInputText(int id) {
        return Objects.requireNonNull(((TextInputEditText) requireView().findViewById(id)).getText()).toString();
    }

    /**
     * Changes the fragment back to the home screen
     */
    private void returnToHomeScreen() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, new HomeFragment(itemRef));
        transaction.commit();
    }
}
