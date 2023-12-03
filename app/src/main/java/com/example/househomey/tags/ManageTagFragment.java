package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.os.BundleCompat;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageTagFragment extends TagFragment {
    private EditText tagEditText;
    private Button addTagButton;

    /**
     * Called to create the dialog, initializing UI components and setting up button listeners.
     *
     * @param savedInstanceState Bundle containing the saved state of the fragment.
     * @return The created AlertDialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = requireActivity().getLayoutInflater().inflate(R.layout.fragment_manage_tags, null);
        this.tagRef = ((MainActivity) requireActivity()).getTagRef();
        final ArrayList<Item> selectedItems = BundleCompat.getParcelableArrayList(getArguments(), "itemList", Item.class);

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

        getTagCollection();

        // Build the dialog window
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Manage Tags")
                .setPositiveButton("Done", (d, which) -> {
                    ApplyTagFragment applyTagFragment = new ApplyTagFragment();
                    Bundle tagArgs = new Bundle();
                    tagArgs.putParcelableArrayList("itemList", selectedItems);
                    applyTagFragment.setArguments(tagArgs);
                    applyTagFragment.show(requireActivity().getSupportFragmentManager(),"tagDialog");
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * Creates a new chip for the new tag that is deletable
     * @param label Name of the tag for the new chip
     */
    protected void makeTagChip(String label) {
        final Chip chip = FragmentUtils.makeChip(label, true, chipGroup, requireContext(), R.drawable.tag_chip, R.color.brown, R.color.brown);
        chip.setOnCloseIconClickListener(v ->
                tagRef.document(label).delete().addOnSuccessListener(result -> {
                    chipGroup.removeView(chip);
                    tagList.removeIf(tag -> tag.getTagLabel().equals(chip.getText().toString()));
                })
        );
    }

    /**
     * Adds a new tag to the ChipGroup and the tag list when addTagButton is clicked.
     * @param tagLabel the label that will go on the tag
     */
    private void addTag(String tagLabel) {
        tagLabel = FragmentUtils.initialCase(tagLabel);
        if (!tagList.stream()
                .map(Tag::getTagLabel)
                .collect(Collectors.toList()).contains(tagLabel)) {
            Tag tag = new Tag(tagLabel, new HashMap<>());
            tagList.add(tag);
            makeTagChip(tagLabel);
            Map<String, Object> data = new HashMap<>();
            data.put("items", new ArrayList<>());
            tagRef.document(tagLabel).set(data);
        }
        tagEditText.getText().clear();
    }
}
