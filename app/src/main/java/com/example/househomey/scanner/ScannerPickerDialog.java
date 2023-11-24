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

    private void launchSerialNumScanner() {
        SNImageScanner.OnImageScannedListener listener;
        Fragment parent = requireParentFragment();
        if (parent instanceof SNImageScanner.OnImageScannedListener) {
            listener = (SNImageScanner.OnImageScannedListener) parent;
        } else {
            throw new ClassCastException(parent + " must implement OnImagePickedListener");
        }
        scanner = new SNImageScanner(getContext(), listener);
        imagePickerDialog.show(getChildFragmentManager(), imagePickerDialog.getTag());
    }

    @Override
    public void onImagePicked(String imageUri) {
        imagePickerDialog.dismiss();
        scanner.scanImage(imageUri);
        dismiss();
    }
}
