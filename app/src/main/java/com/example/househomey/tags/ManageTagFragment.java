package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.tagRef = ((MainActivity) requireActivity()).getTagRef();

        Bundle args = getArguments();
        final ArrayList<Item> selectedItems = BundleCompat.getParcelableArrayList(args, "itemList", Item.class);

        // Initialize AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_manage_tags, null);

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

        return builder
                .setView(rootView)
                .setTitle("Manage Tags")
                .setPositiveButton("Done", (dialog, which) -> {
                    ApplyTagFragment applyTagFragment = new ApplyTagFragment();

                    Bundle tagArgs = new Bundle();
                    tagArgs.putParcelableArrayList("itemList", selectedItems);
                    applyTagFragment.setArguments(tagArgs);
                    applyTagFragment.show(requireActivity().getSupportFragmentManager(),"tagDialog");
                })
                .create();
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
