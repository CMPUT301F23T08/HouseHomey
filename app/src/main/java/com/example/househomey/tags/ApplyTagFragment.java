package com.example.househomey.tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.os.BundleCompat;

import com.example.househomey.Item;
import com.example.househomey.MainActivity;
import com.example.househomey.R;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DialogFragment that allows users to add new tags
 * @author Matthew Neufeld
 */
public class ApplyTagFragment extends TagFragment implements Serializable {
    private ArrayList<Item> selectedItems;


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
        selectedItems = BundleCompat.getParcelableArrayList(args, "itemList", Item.class);

        // Initialize AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_apply_tags, null);

        // Initialize UI components
        chipGroup = rootView.findViewById(R.id.chip_group_labels);

        getTagCollection();

        return builder
                .setView(rootView)
                .setTitle("Tags")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Manage", (d, w) -> {
                    ManageTagFragment manageTagFragment = new ManageTagFragment();

                    Bundle tagArgs = new Bundle();
                    tagArgs.putParcelableArrayList("itemList", selectedItems);
                    manageTagFragment.setArguments(tagArgs);
                    manageTagFragment.show(requireActivity().getSupportFragmentManager(),"tagDialog");
                })
                .setPositiveButton("Apply Tags", (dialog, which) -> applyItemsToTag(rootView, selectedItems))
                .create();
    }

    /**
     * Creates a new chip for the new tag
     * @param label Name of the tag for the new chip
     */
    protected void makeTagChip(String label) {
        FragmentUtils.makeChip(label, false, chipGroup, requireContext(), R.drawable.tag_chip, R.color.brown, R.color.brown, true);
    }

    /**
     * Save items to Firestore for the given tags.
     * @param selectedItems Items to which the tags will be applied
     */
    private void applyItemsToTag(View rootView, ArrayList<Item> selectedItems) {
        ArrayList<String> selectedTags = new ArrayList<>();
        for (int id: chipGroup.getCheckedChipIds()) {
            Chip chip = rootView.findViewById(id);
            selectedTags.add(chip.getText().toString());
        }
        if (!selectedItems.isEmpty()) {
            List<String> idList = selectedItems.stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            for (String tag : selectedTags) {
                batch.update(tagRef.document(tag),
                        "items", FieldValue.arrayUnion(idList.toArray()));
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

}
