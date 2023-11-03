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

public class AddItemFragment extends Fragment {
    private Date dateAcquired;
    private TextInputEditText dateTextView;
    private final CollectionReference itemRef;

    public AddItemFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

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

    private String getInputText(int id) {
        return Objects.requireNonNull(((TextInputEditText) requireView().findViewById(id)).getText()).toString();
    }

    private void returnToHomeScreen() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, new HomeFragment(itemRef));
        transaction.commit();
    }
}
