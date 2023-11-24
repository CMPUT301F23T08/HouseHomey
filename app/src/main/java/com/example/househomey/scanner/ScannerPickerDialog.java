package com.example.househomey.scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.househomey.R;
import com.example.househomey.form.ImagePickerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class ScannerPickerDialog extends BottomSheetDialogFragment implements ImagePickerDialog.OnImagePickedListener {
    ImageScanner scanner;
    ImagePickerDialog imagePickerDialog;

    /**
     * Create new Scanner picker dialog
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_scanning_choices, container, false);

        // Set up listeners to open the camera/gallery
        Button barcodeButton = rootView.findViewById(R.id.barcode_button);
        Button serialNumButton = rootView.findViewById(R.id.serialNum_button);
        //barcodeButton.setOnClickListener(v -> launchImageIntent(true));
        serialNumButton.setOnClickListener(v -> launchSerialNumScanner());
        imagePickerDialog = new ImagePickerDialog();
        return rootView;
    }

    /**
     * Launches a new serial num scanner and brings up image picker dialog
     */
    private void launchSerialNumScanner() {
        SNImageScanner.OnImageScannedListener listener;
        Fragment parent = requireParentFragment();
        if (parent instanceof SNImageScanner.OnImageScannedListener) {
            listener = (SNImageScanner.OnImageScannedListener) parent;
        } else {
            throw new ClassCastException(parent + " must implement OnImagePickedListener");
        }
        scanner = new SNImageScanner(getContext(), listener);
        dismiss();
        imagePickerDialog.show(getChildFragmentManager(), imagePickerDialog.getTag());
    }

    /**
     * Scans a given image using the appropriate scanner
     * @param imageUri uri of the image to scan
     */
    @Override
    public void onImagePicked(String imageUri) {
        imagePickerDialog.dismiss();
        scanner.scanImage(imageUri);
    }
}
