package com.example.househomey.form;

import static com.example.househomey.utils.FragmentUtils.createDatePicker;
import static java.util.Objects.requireNonNull;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.scanner.SNImageScanner;
import com.example.househomey.scanner.ScannerPickerDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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
public abstract class ItemFormFragment extends Fragment implements ImagePickerDialog.OnImagePickedListener, PhotoAdapter.OnAddButtonClickListener, SNImageScanner.OnImageScannedListener {
    protected Date dateAcquired;
    protected CollectionReference itemRef;
    protected ArrayList<String> photoUris = new ArrayList<>();
    protected PhotoAdapter photoAdapter;
    private TextInputEditText dateTextView;
    private TextInputEditText sNTextView;
    private ImagePickerDialog imagePickerDialog;

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
        sNTextView = rootView.findViewById(R.id.add_item_serial_number);
        View scanButton = rootView.findViewById(R.id.add_item_scan_button);
        scanButton.setOnClickListener(v -> launchScannerPicker());
        return rootView;
    }

    /**
     * Launches the scanner picker dialog
     */
    private void launchScannerPicker() {
        ScannerPickerDialog dialog = new ScannerPickerDialog();
        dialog.show(getChildFragmentManager(), dialog.getTag());
    }

    /**
     * Validates the user input and constructs an Item object if the input is valid.
     * If validation succeeds, uploads new attached photos to Firebase Cloud Storage.
     *
     * @param itemId The unique identifier of the item, if it exists, or an empty string for new items.
     * @return An Item object representing the validated item data, or null if validation fails.
     */
    protected Item prepareItem(String itemId) {
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
        Item item;
        try {
            item = new Item(itemId, data);
        } catch (NullPointerException e) {
            return null;
        }

        // Item is valid, upload new photos (if any) to Cloud Storage
        photoUris.replaceAll(this::uploadImageToFirebase);
        item.setPhotoIds(photoUris);
        return item;
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

    /**
     * Initializes photo components for the fragment (photo adapter,
     * and image picker dialog) as well as sets this fragment to the components' listeners.
     *
     * @param rootView The root view of the fragment.
     */
    private void initAddImageHandler(View rootView) {
        imagePickerDialog = new ImagePickerDialog();
        photoAdapter = new PhotoAdapter(getContext(), photoUris, this);
        ((RecyclerView) rootView.findViewById(R.id.add_photo_grid)).setAdapter(photoAdapter);
    }

    /**
     * Callback method for when the Add button in the photo gallery is clicked.
     */
    @Override
    public void onAddButtonClicked() {
        imagePickerDialog.show(getChildFragmentManager(), imagePickerDialog.getTag());
    }

    /**
     * Callback method for handling the image URI returned from image picker dialog.
     *
     * @param imageUri The URI of the selected/taken image.
     */
    @Override
    public void onImagePicked(String imageUri) {
        imagePickerDialog.dismiss();
        photoUris.add(imageUri);
        photoAdapter.notifyItemInserted(photoAdapter.getItemCount() - 1);
    }

    /**
     * Sets the result of the Serial Number scanning in the serial numbner text view
     * @param serialNumber the scanned serial number to set
     */
    @Override
    public void onSNScanningComplete(String serialNumber) {
        sNTextView.setText(serialNumber);
    }

    /**
     * Uploads a local image to Firebase Cloud Storage and returns its UUID.
     *
     * @param imageUri The local URI of the image to be uploaded.
     * @return The UUID of the uploaded image.
     */
    private String uploadImageToFirebase(String imageUri) {
        if (imageUri.contains("content://") || imageUri.contains("file://")) {
            // New image to upload, create a unique storage reference
            String imageId = UUID.randomUUID().toString();
            StorageReference imageRef = ((MainActivity) requireActivity()).getImageRef(imageId);

            // Upload the image to Cloud Storage
            imageRef.putFile(Uri.parse(imageUri))
                    .addOnSuccessListener(taskSnapshot -> Log.d("IMAGE_UPLOAD", "Successfully uploaded image to: " + taskSnapshot.getStorage()))
                    .addOnFailureListener(e -> Log.e("IMAGE_UPLOAD", "Failed to upload image: " + e));
            return imageId;
        }
        // Already uploaded to Firebase
        return imageUri;
    }
}
