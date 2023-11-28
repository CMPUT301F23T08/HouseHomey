package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment that allows users to add new tags
 * @author Matthew Neufeld
 */
public class TagFragment extends DialogFragment {
    private ChipGroup chipGroup;
    private EditText tagEditText;
    private Button addTagButton;
    private Chip chip;
    private List<Tag> tagList = new ArrayList<>();
    private final ArrayList<Item> selectedItems;
    private CollectionReference tagRef;

    /**
     * Constructor for TagFragment
     * @param selectedItems items selected to be deleted
     */
    public TagFragment(ArrayList<Item> selectedItems) {
        this.selectedItems = selectedItems;
    }

    /**
     * Called to create the dialog, initializing UI components and setting up button listeners.
     *
     * @param savedInstanceState Bundle containing the saved state of the fragment.
     * @return The created AlertDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.tagRef = ((MainActivity) requireActivity()).getTagRef();
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
                .setPositiveButton("Apply Tags", (dialog, which) -> ApplyItemsToTag(selectedItems))
                .create();
    }

    /**
     * Adds a new tag to the ChipGroup and the tag list when addTagButton is clicked.
     * @param tagLabel the label that will go on the tag
     */
    private void addTag(String tagLabel) {
        chip = FragmentUtils.makeChip(tagLabel, false, chipGroup, requireContext(), R.color.white, R.color.black, R.color.black);
        Tag tag = new Tag(tagLabel);
        tagList.add(tag);
        tagEditText.getText().clear();
    }

    /**
     * Save items to Firestore for the given tags.
     * @param selectedItems Items to which the tags will be applied
     */
    private void ApplyItemsToTag(ArrayList<Item> selectedItems) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (Tag tag : tagList) {
            batch.update(tagRef.document(tag.getId()), "items", selectedItems);
        }
        batch.commit()
                .addOnSuccessListener((result) -> {
                    Log.i("Firestore", "Items successfully applied to tag");
                })
                .addOnFailureListener((error) -> {
                    Log.e("Firestore", "Failed to apply items to tag.", error);
                });
    }
}
