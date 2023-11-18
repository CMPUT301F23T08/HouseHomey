package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.househomey.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * DialogFragment that allows users to add new tags
 * @author Matthew Neufeld
 */
public class TagFragment extends DialogFragment {

    // Declare UI components
    private ChipGroup chipGroup;
    private EditText tagEditText;
    private Button addTagButton;

    /**
     * Called to create the dialog, initializing UI components and setting up button listeners.
     * @param savedInstanceState Bundle containing the saved state of the fragment.
     * @return The created AlertDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initialize AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_tags, null);

        // Initialize UI components
        chipGroup = rootView.findViewById(R.id.chip_group_labels);
        tagEditText = rootView.findViewById(R.id.tag_edit_text);
        addTagButton = rootView.findViewById(R.id.add_tag_button);

        // Set up button click listener to add tags
        addTagButton.setOnClickListener(v -> addTag());

        // Set up the AlertDialog
        builder.setView(rootView);
        builder.setTitle("Tags");
        builder.setNeutralButton("Cancel", null);
        builder.setPositiveButton("Apply Tags", null);

        return builder.create();
    }

    /**
     * Adds a new tag to the ChipGroup when addTagButton is clicked.
     */
    private void addTag() {
        String tagText = tagEditText.getText().toString().trim();

        // Check if the tag is not empty
        if (!tagText.isEmpty()) {
            // Create a new Chip and set its properties
            Chip chip = new Chip(requireContext());
            chip.setText(tagText);
            chip.setCloseIconVisible(false);
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip)); // for deleting tags, keep for later

            // Add the chip to the ChipGroup and clear the EditText
            chipGroup.addView(chip);
            tagEditText.getText().clear();
        } else {
            // Display a toast if the tag is empty
            Toast.makeText(requireContext(), "Tag cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
