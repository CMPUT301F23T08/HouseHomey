package com.example.househomey.form;

import static android.app.Activity.RESULT_OK;
import static com.example.househomey.utils.FragmentUtils.createDatePicker;
import static java.util.Objects.requireNonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * This abstract class serves as a base for creating and managing both Add and Edit Item forms.
 * It provides common functionality for handling user input and date selection.
 *
 * @author Owen Cooke
 */
public abstract class ItemFormFragment extends Fragment {
    protected Date dateAcquired;
    private TextInputEditText dateTextView;
    protected CollectionReference itemRef;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    protected ArrayList<String> photoUris = new ArrayList<>();
    protected PhotoAdapter photoAdapter;

    /**
     * Creates the basic view for a validated Item form.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return A View representing the Item form with validation listeners.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        initAddImageHandler(rootView);
        // Get item collection reference from main activity
        itemRef = ((MainActivity) requireActivity()).getItemRef();
        return rootView;
    }

    private void addNewPhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(galleryIntent);
    }

    protected String uploadImageToFirebase(String imageUri) {
        // Create a unique reference for the image in Cloud Storage
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageId);

        // Upload the image to Cloud Storage
        // TODO: discuss error handling or logging
        imageRef.putFile(Uri.parse(imageUri))
                .addOnSuccessListener(taskSnapshot -> {

                })
                .addOnFailureListener(e -> {
                });
        return imageId;
    }

    /**
     * Validates the user input and constructs an Item object if the input is valid.
     *
     * @param itemId The unique identifier of the item, if it exists, or an empty string for new items.
     * @return An Item object representing the validated item data, or null if validation fails.
     */
    protected Item validateItem(String itemId) {
        // Check that required fields are filled before validating
        boolean invalidDesc = isRequiredFieldEmpty(R.id.add_item_description_layout);
        boolean invalidDate = isRequiredFieldEmpty(R.id.add_item_date_layout);
        boolean invalidCost = isRequiredFieldEmpty(R.id.add_item_cost_layout);
        if (invalidDesc || invalidDate || invalidCost) return null;

        // Create map with form data
        HashMap<String, Object> data = new HashMap<>();
        data.put("description", getInputText(R.id.add_item_description));
        data.put("acquisitionDate", new Timestamp(dateAcquired));
        data.put("cost", getInputText(R.id.add_item_cost));
        data.put("make", getInputText(R.id.add_item_make));
        data.put("model", getInputText(R.id.add_item_model));
        data.put("serialNumber", getInputText(R.id.add_item_serial_number));
        data.put("comment", getInputText(R.id.add_item_comment));
        data.put("photos", photoUris);

        // Ensure that form data can be used to create a valid Item
        try {
            return new Item(itemId, data);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Checks whether the text input for a required form field is empty.
     * Additionally, if empty, it sets an inline error message on the input field.
     * If not empty, it removes the inline error.
     *
     * @param id Id of the required field's TextInputLayout
     * @return a boolean indicating if the field is empty
     */
    private boolean isRequiredFieldEmpty(int id) {
        TextInputLayout textInputLayout = getView().findViewById(id);
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText().toString().trim())) {
            textInputLayout.setError("This field is required");
            return true;
        }
        textInputLayout.setError(null);
        return false;
    }


    /**
     * Gets the user input as a string from a given TextInputEditText
     *
     * @param id Id of the TextInputEditText object
     * @return The user input String
     * @throws NullPointerException if the input text is null
     */
    private String getInputText(int id) {
        return requireNonNull(((TextInputEditText) requireView().findViewById(id)).getText()).toString();
    }

    /**
     * Initializes and configures a Date Picker for selecting past/present acquisition dates.
     *
     * @param rootView The root view of the UI where the date picker is to be displayed.
     */
    protected void initDatePicker(View rootView) {
        dateTextView = rootView.findViewById(R.id.add_item_date);

        MaterialDatePicker<Long> datePicker = createDatePicker();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateAcquired = new Date(selection);
            dateTextView.setText(datePicker.getHeaderText());
            ((TextInputLayout) rootView.findViewById(R.id.add_item_date_layout)).setError(null);
        });

        // Show DatePicker when date field selected
        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
    }

    /**
     * Initializes text validators for required input fields on the add item form.
     *
     * @param rootView The root view of the UI where the input fields are located.
     */
    protected void initTextValidators(View rootView) {
        TextInputEditText descView = rootView.findViewById(R.id.add_item_description);
        TextInputEditText costView = rootView.findViewById(R.id.add_item_cost);

        // Add text watchers for empty input to both description and cost
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

        // Add listener for rounding cost to 2 decimals
        costView.setOnFocusChangeListener((v, b) -> {
            String costString = costView.getText().toString();
            try {
                costString = new BigDecimal(costString).setScale(2, RoundingMode.HALF_UP).toString();
                costView.setText(costString);
            } catch (NumberFormatException ignored) {
            }
        });
    }

    private void initAddImageHandler(View rootView) {
        // Set up adapter for gallery RecyclerView
        photoAdapter = new PhotoAdapter(getContext(), photoUris);
        ((RecyclerView) rootView.findViewById(R.id.add_photo_grid)).setAdapter(photoAdapter);

        // Add listeners for photo adding
        rootView.findViewById(R.id.add_photo_button).setOnClickListener(v -> addNewPhoto());
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                // User selected an image from their photos app
                Uri imageUri = result.getData().getData();
                photoUris.add(imageUri.toString());
                photoAdapter.notifyItemInserted(photoAdapter.getItemCount() - 1);
            }
        });
    }
}
