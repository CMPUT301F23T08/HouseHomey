package com.example.househomey.tags;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TagFragment extends DialogFragment {
    protected CollectionReference tagRef;
    protected List<Tag> tagList = new ArrayList<>();
    protected ChipGroup chipGroup;

    abstract void makeTagChip(String label);


    /**
     * Gets a snapshot of the tags collection in Firestore
     */
    protected void getTagCollection() {
        tagRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Tag tag = new Tag(document.getId(), document.getData());
                if (!tagList.stream()
                        .map(Tag::getTagLabel)
                        .collect(Collectors.toList()).contains(tag.getTagLabel())) {
                    tagList.add(tag);
                    makeTagChip(tag.getTagLabel());
                }
            }
        });
    }
}
