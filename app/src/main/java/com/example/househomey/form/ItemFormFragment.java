package com.example.househomey.form;

import static com.example.househomey.utils.FragmentUtils.createDatePicker;
import static com.example.househomey.utils.FragmentUtils.isValidUUID;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househomey.item.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.utils.FragmentUtils;
import com.example.househomey.scanner.BarcodeImageScanner;
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
public abstract class ItemFormFragment extends Fragment implements ImagePickerDialog.OnImagePickedListener,
        PhotoAdapter.OnButtonClickListener, SNImageScanner.OnImageScannedListener,
        BarcodeImageScanner.OnBarcodeScannedListener {
    protected Date dateAcquired;
    private TextInputEditText dateTextView;
    protected CollectionReference itemRef;
    protected ArrayList<String> photoUris = new ArrayList<>();
    protected PhotoAdapter photoAdapter;
    private TextInputEditText sNTextView;
    private TextInputEditText descriptionTextView;
    private final ArrayList<String> photosToDelete = new ArrayList<>();
    private ImagePickerDialog imagePickerDialog;
    private int count = 0;
    private boolean failedToUpload = false;
    private long expectedCount;

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
        descriptionTextView = rootView.findViewById(R.id.add_item_description);
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
     * Writes data to Firestore.
     * Subclasses must implement this method to define the specific logic for writing data to Firestore.
     */
    public abstract void writeToFirestore();

    /**
     * Prepares the item for storage by handling photo-related tasks.
     * This method calculates the expected count of valid photo URIs, uploads new photos (if any)
     * to Cloud Storage, sets the photo IDs on the provided item, removes deleted photos (if any)
     * from Cloud Storage, and writes the item to Firestore if no valid photos are present.
     *
     * @param item The item to be prepared, which may include associated photos.
     */
    protected void prepareItem(Item item) {
        expectedCount = photoUris.stream().filter(uri -> !FragmentUtils.isValidUUID(uri)).count();
        // Upload new photos (if any) to Cloud Storage
        photoUris.replaceAll(this::uploadImageToFirebase);
        item.setPhotoIds(photoUris);

        // Remove deleted photos (if any) from Cloud Storage
        for (String imageId : photosToDelete) {
            StorageReference imageRef = ((MainActivity) requireActivity()).getImageRef(imageId);
            imageRef.delete()
                    .addOnSuccessListener(taskSnapshot -> Log.d("IMAGE_DELETE", "Successfully removed image from: " + imageRef))
                    .addOnFailureListener(e -> Log.e("IMAGE_DELETE", "Failed to delete image from Cloud Storage: " + e));
        }
        if (expectedCount == 0) {
            writeToFirestore();
        }
    }

    /**
     * Validates and creates an Item using form data.
     * Checks that required fields are filled, creates a map with form data, and attempts
     * to create a valid Item. Returns null if validation fails or if the creation
     * of the Item results in a NullPointerException.
     *
     * @param itemId The ID of the item to be validated, can be null for new items.
     * @return A validated Item or null if validation fails.
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
            Log.d("Item", "Tried to create an invalid item.");
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
        textInputLayout.setErrorEnabled(false);
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
            ((TextInputLayout) rootView.findViewById(R.id.add_item_date_layout)).setErrorEnabled(false);
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
     * Sets the result of the Serial Number scanning in the serial number text view
     * @param serialNumber the scanned serial number to set
     */
    @Override
    public void onSNScanningComplete(String serialNumber) {
        sNTextView.setText(serialNumber);
    }

    /**
     * Sets the item description field to the decoded barcode value after scanning image
     * @param description the decoded barcode value to set as description
     */
    @Override
    public void onBarcodeOKPressed(String description) {
        descriptionTextView.setText(description);
    }

    /**
     * Sets the item serial number field to the decoded barcode value after scanning image
     * @param serialNumber the decoded barcode value to set as serial number
     */
    @Override
    public void onSerialNumberOKPressed(String serialNumber) {sNTextView.setText(serialNumber);}

    /**
     * Uploads an image to Firebase Cloud Storage.
     * If the image URI is not a valid UUID, it creates a unique storage reference,
     * uploads the image to Cloud Storage, and returns the generated image ID.
     * If the image URI is a valid UUID, it is assumed that the image is already uploaded,
     * and the original image URI is returned.
     * @param imageUri The URI of the image to be uploaded.
     * @return The image ID if the image is uploaded, or the original image URI if already uploaded.
     */
    private String uploadImageToFirebase(String imageUri) {
        if (!isValidUUID(imageUri)) {
            // New image to upload, create a unique storage reference
            String imageId = UUID.randomUUID().toString();
            StorageReference imageRef = ((MainActivity) requireActivity()).getImageRef(imageId);

            // Upload the image to Cloud Storage
            imageRef.putFile(Uri.parse(imageUri))
                    .addOnSuccessListener(taskSnapshot -> {
                        count++;
                        if (count == expectedCount) {
                            if (failedToUpload) {
                                Toast.makeText(requireActivity().getApplicationContext(),
                                        "Failed to upload images.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                writeToFirestore();
                            }
                        }
                        Log.d("IMAGE_UPLOAD", "Successfully uploaded image to: " + taskSnapshot.getStorage());
                    })
                    .addOnFailureListener(e -> {
                        failedToUpload = true;
                        count++;
                        if (count == expectedCount) {
                            Toast.makeText(requireActivity().getApplicationContext(),
                                    "Failed to upload images.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            return imageId;
        }
        // Already uploaded to Firebase
        return imageUri;
    }

    /**
     * Callback method for when the Delete button on a photo is clicked.
     * @param position The integer index of the clicked position in the adapter.
     */
    @Override
    public void onDeleteButtonClicked(int position) {
        // Keep track of Cloud Storage IDs to delete when confirm button clicked
        String imageId = photoUris.get(position);
        if (isValidUUID(imageId)) {
            photosToDelete.add(imageId);
        }
        // Remove photo from adapter
        photoUris.remove(position);
        photoAdapter.notifyItemRemoved(position);
    }

    /**
     * Clears all the text fields and the photos in the form
     */
    protected void clearDataFields() {
        View formView = getView();
        int numUris = photoUris.size();
        photoUris.clear();
        photoAdapter.notifyItemRangeRemoved(0,numUris);
        ((EditText) formView.findViewById(R.id.add_item_description)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_date)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_cost)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_make)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_model)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_serial_number)).setText("");
        ((EditText) formView.findViewById(R.id.add_item_comment)).setText("");
    }
}
