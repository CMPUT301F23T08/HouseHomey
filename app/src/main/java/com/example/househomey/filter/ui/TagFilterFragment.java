package com.example.househomey.filter.ui;

import android.content.Context;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.example.househomey.Item;
import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.TagFilter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagFilterFragment extends FilterFragment {
    protected TagFilter tagFilter;
    private final ChipGroup tagChipGroup;
    private Map<String, Boolean> tagSelectionMap = new HashMap<>();

    public TagFilterFragment(String title, View contentView, FilterCallback filterCallback, ArrayList<Item> itemList) {
        super(title, contentView, filterCallback);
        tagChipGroup = contentView.findViewById(R.id.chip_group_labels);
        populateTagChipGroup(itemList);
    }

    public TagFilterFragment(String title, View contentView, FilterCallback filterCallback, ArrayList<Item> itemList, TagFilter tagFilter) {
        super(title, contentView, filterCallback);
        this.tagFilter = tagFilter;
        tagSelectionMap = tagFilter.tagSelectionMap;
        tagChipGroup = contentView.findViewById(R.id.chip_group_labels);
        tagChipGroup.removeAllViews();
        populateTagChipGroup(itemList);
    }

    private void populateTagChipGroup(ArrayList<Item> itemList) {
        Set<String> existingTags = new HashSet<>();
        for (Item item : itemList) {
            for (String tag : item.getTags()) {
                if (existingTags.add(tag)) {
                    Chip chip = createTagChip(tag);
                    tagChipGroup.addView(chip);
                }
            }
        }
    }

    private Chip createTagChip(String tag) {
        Context context = contentView.getContext();
        Chip chip = new Chip(context);
        chip.setText(tag);
        chip.setCheckable(true);
        chip.setChipStrokeColorResource(R.color.brown);
        chip.setTextColor(ContextCompat.getColor(context, R.color.brown));

        autofillLastSelectedChip(chip, tag);
        setChipListener(chip, tag);
        return chip;
    }

    private void autofillLastSelectedChip(Chip chip, String tag) {
        boolean isSelected = tagSelectionMap.containsKey(tag) && Boolean.TRUE.equals(tagSelectionMap.get(tag));
        chip.setChecked(isSelected);
        chip.setChipBackgroundColorResource(isSelected ? R.color.grey : R.color.white);
        tagSelectionMap.put(tag, isSelected);
    }

    private void setChipListener(Chip chip, String tag) {
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tagSelectionMap.put(tag, isChecked);
            chip.setChipBackgroundColorResource(isChecked ? R.color.grey : R.color.white);
        });
    }

    @Override
    public void getFilterInput() {
        this.tagFilter = new TagFilter(this.tagSelectionMap);
        filterCallback.onFilterApplied(this.tagFilter);
        dismiss();
    }

    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.tagFilter);
        dismiss();
    }
}
