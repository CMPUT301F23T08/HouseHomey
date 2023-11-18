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
import java.util.Map;

public class TagFilterFragment extends FilterFragment {
    protected TagFilter tagFilter;
    private ChipGroup tagChipGroup;
    private Map<String, Boolean> tagSelectionMap = new HashMap();

    public TagFilterFragment(String title, View contentView, FilterCallback filterCallback, ArrayList<Item> itemList) {
        super(title, contentView, filterCallback);
        tagChipGroup = contentView.findViewById(R.id.chip_group_labels);

        for (Item item : itemList) {
            for (String tag : item.getTags()) {
                if (!tagSelectionMap.containsKey(tag)) {
                    Context context = contentView.getContext();
                    Chip chip = new Chip(context);
                    chip.setText(tag);
                    chip.setCheckable(true);
                    chip.setChipBackgroundColorResource(R.color.white);
                    chip.setChipStrokeColorResource(R.color.brown);
                    chip.setTextColor(ContextCompat.getColor(context, R.color.brown));

                    tagSelectionMap.put(tag, false);
                    tagChipGroup.addView(chip);

                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        int backgroundColor = isChecked ? R.color.grey : R.color.white;
                        chip.setChipBackgroundColorResource(backgroundColor);
                        tagSelectionMap.put(tag, isChecked);
                    });
                }
            }
        }
    }

    @Override
    public void getFilterInput() {
        tagFilter = new TagFilter(tagSelectionMap);
        filterCallback.onFilterApplied(tagFilter);
        dismiss();
    }

    @Override
    public void resetFilter() {
        // TODO: logic for resetting current filter
    }
}
