package com.example.househomey.filter.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.KeywordFilter;
import com.example.househomey.filter.model.MakeFilter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


/**
 * Fragment for applying "Keyword" filter criteria.
 */
public class KeywordFilterFragment extends FilterFragment {

    ArrayList<String> chipValues = new ArrayList<>();

    /**
     * Constructs a new KeywordFilterFragment.
     * @param title         The title of the "Keyword" filter dialog.
     * @param contentView   The content view of the "Keyword" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     */
    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    /**
     * Extracts the "Keyword" filter edit text input and applies the filter.
     */
    @Override
    public void getFilterInput() {
        // TODO: get the filter properties from fragment inputs
        ChipGroup chipGroup = contentView.findViewById(R.id.chip_group_labels);
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View view = chipGroup.getChildAt(i);
            if (view instanceof Chip) {
                Chip chip = (Chip) view;
                chipValues.add(chip.getText().toString());
            }
        }

        KeywordFilter keywordFilter = new KeywordFilter(chipValues);
        filterCallback.onFilterApplied(keywordFilter);

        dismiss();
    }
}
