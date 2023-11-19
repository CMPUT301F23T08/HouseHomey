package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.househomey.R;
import com.example.househomey.utils.FragmentUtils;
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
    private Chip chip;

    /**
     * Called to create the dialog, initializing UI components and setting up button listeners.
     *
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
        addTagButton.setOnClickListener(v -> {
            String tagLabel = tagEditText.getText().toString().trim();
            if (!tagLabel.isEmpty())
                addTag(tagLabel);
        });

        return builder
                .setView(rootView)
                .setTitle("Tags")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Apply Tags", null)
                .create();
    }

    /**
     * Adds a new tag to the ChipGroup when addTagButton is clicked.
     * @param tagLabel the label that will go on the tag
     */
    private void addTag(String tagLabel) {
        chip = FragmentUtils.makeChip(tagLabel, false, chipGroup, requireContext(), R.color.white, R.color.black, R.color.black);
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip)); // for deleting tags - leave for now -  we will discuss what we want to do
        tagEditText.getText().clear();
    }
}
