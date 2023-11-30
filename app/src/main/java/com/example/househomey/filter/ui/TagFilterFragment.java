/**
 * {@code TagFilterFragment} is a subclass of {@link FilterFragment} that provides filtering
 * functionality based on tags using {@link TagFilter}.
 */
package com.example.househomey.filter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.househomey.Item;
import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.MakeFilter;
import com.example.househomey.filter.model.TagFilter;
import com.example.househomey.tags.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a fragment for tag-based filtering. It extends {@link FilterFragment}
 * and implements methods to manage and interact with tag filters.
 * @author Jared Drueco
 */
public class TagFilterFragment extends FilterFragment {
    protected TagFilter tagFilter;
    private ChipGroup tagChipGroup;
    private ArrayList<Tag> tagList = new ArrayList<>();
    private Map<String, Boolean> tagSelectionMap = new HashMap<>();
    /**
     * Creates dialog for tag filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for tag filter fragment
     */
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_tags, null);
        AlertDialog.Builder builder = createBuilder();
        Dialog dialog = builder.setTitle("Modify Tag Filter").setView(contentView).create();

        if (getArguments() != null) {
            this.filterCallback = getArguments().getSerializable("callback", FilterCallback.class);
            this.tagFilter = getArguments().getSerializable("filter", TagFilter.class);
            this.tagList = getArguments().getSerializable("tags", ArrayList.class);
            if (this.tagFilter != null)
                tagSelectionMap = tagFilter.tagSelectionMap;
        }

        tagChipGroup = contentView.findViewById(R.id.chip_group_labels);
        populateTagChipGroup(tagList);

        return dialog;
    }

    /**
     * Populates the tag {@link ChipGroup} with tags from the provided item list.
     *
     * @param tagList The list of tags.
     */
    private void populateTagChipGroup(ArrayList<Tag> tagList) {
        Set<String> existingTags = new HashSet<>();
        for (Tag tag: tagList) {
            if (existingTags.add(tag.getTagLabel())) {
                Chip chip = createTagChip(tag.getTagLabel());
                tagChipGroup.addView(chip);
            }
        }
    }

    /**
     * Creates a new {@link Chip} for the given tag.
     *
     * @param tag The tag for which the chip is created.
     * @return The created chip.
     */
    private Chip createTagChip(String tag) {
        Context context = contentView.getContext();
        Chip chip = new Chip(context);
        chip.setText(tag);
        chip.setCheckable(true);
        chip.setChipStrokeColorResource(R.color.brown);
        chip.setTextColor(ContextCompat.getColor(context, R.color.brown));

        autoFillLastSelectedChip(chip, tag);
        setChipListener(chip, tag);
        return chip;
    }

    /**
     * Auto fills the last selected state of the chip based on the tagSelectionMap.
     *
     * @param chip The chip to be auto filled.
     * @param tag  The tag associated with the chip.
     */
    private void autoFillLastSelectedChip(Chip chip, String tag) {
        boolean isSelected = tagSelectionMap.containsKey(tag) && Boolean.TRUE.equals(tagSelectionMap.get(tag));
        chip.setChecked(isSelected);
        chip.setChipBackgroundColorResource(isSelected ? R.color.grey : R.color.white);
        tagSelectionMap.put(tag, isSelected);
    }

    /**
     * Sets the {@link Chip} listener to handle checked state changes.
     *
     * @param chip The chip to set the listener for.
     * @param tag  The tag associated with the chip.
     */
    private void setChipListener(Chip chip, String tag) {
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tagSelectionMap.put(tag, isChecked);
            chip.setChipBackgroundColorResource(isChecked ? R.color.grey : R.color.white);
        });
    }

    /**
     * Gets the filter input, creates a {@link TagFilter}, and notifies the callback.
     */
    @Override
    public void getFilterInput() {
        this.tagFilter = new TagFilter(this.tagSelectionMap);
        filterCallback.onFilterApplied(this.tagFilter);
        dismiss();
    }

    /**
     * Resets the filter to its initial state and notifies the callback.
     */
    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.tagFilter);
        dismiss();
    }
}
