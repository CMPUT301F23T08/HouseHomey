package com.example.househomey;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;

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
        dateTextView = rootView.findViewById(R.id.add_item_date);

        // Create DatePicker with dates restricted to past/present
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis());
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateAcquired = new Date(selection);
            dateTextView.setText(datePicker.getHeaderText());
            ((TextInputLayout) getView().findViewById(R.id.add_item_date_layout)).setError(null);
        });

        // Show DatePicker when date field selected
        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));

        TextInputEditText descView = rootView.findViewById(R.id.add_item_description);
        TextInputEditText costView = rootView.findViewById(R.id.add_item_cost);
        TextWatcher emptyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.equals(descView.getEditableText())) {
                    isRequiredFieldEmpty(R.id.add_item_description_layout);
                } else if (editable == costView.getEditableText()) {
                    isRequiredFieldEmpty(R.id.add_item_cost_layout);
                }
            }
        };
        descView.addTextChangedListener(emptyTextWatcher);
        costView.addTextChangedListener(emptyTextWatcher);

        // Add listener for confirm and back buttons
        rootView.findViewById(R.id.add_item_confirm_button).setOnClickListener(v -> addItem());
        rootView.findViewById(R.id.add_item_back_button).setOnClickListener(v -> returnToHomeScreen());

        return rootView;
    }

    private boolean isRequiredFieldEmpty(int id) {
        TextInputLayout textInputLayout = (TextInputLayout) getView().findViewById(id);
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText().toString().trim())) {
            textInputLayout.setError("This field is required");
            return true;
        }
        textInputLayout.setError(null);
        return false;
    }

    /**
     * Adds the user input data to the firestore database
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
