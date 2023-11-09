package com.example.househomey.filter.ui;

import android.os.Bundle;
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
import java.util.Arrays;
import java.util.List;


/**
 * Fragment for applying "Keyword" filter criteria.
 */
public class KeywordFilterFragment extends FilterFragment {
    private ChipGroup chipGroup = contentView.findViewById(R.id.chip_group_labels);
    private ArrayList<String> chipTextVals = new ArrayList<>();
    protected KeywordFilter keywordFilter;
    Button addButton = contentView.findViewById(R.id.add_keyword_button);
    EditText keyWords = contentView.findViewById(R.id.keyword_edit_text);

    /**
     * Constructs a new KeywordFilterFragment.
     * @param title         The title of the "Keyword" filter dialog.
     * @param contentView   The content view of the "Keyword" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     * @param keywordFilter The previous filter instance
     */
    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback, KeywordFilter keywordFilter) {
        super(title, contentView, filterCallback);
        this.keywordFilter = keywordFilter;
        autoFillFilter(this.keywordFilter.keyWords);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> keyWordArray = Arrays.asList(keyWords.getText().toString().split(" "));
                for (String label : keyWordArray) {
                    Chip chip = new Chip(contentView.getContext());
                    chip.setText(label);
                    chipGroup.addView(chip);
                    chipTextVals.add(chip.getText().toString());
                }
                keyWords.setText("");
            }
        });
    }

    /**
     * Constructs a new KeywordFilterFragment.
     * @param title         The title of the "Keyword" filter dialog.
     * @param contentView   The content view of the "Keyword" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     */
    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> keyWordArray = Arrays.asList(keyWords.getText().toString().split(" "));
                for (String label : keyWordArray) {
                    Chip chip = new Chip(contentView.getContext());
                    chip.setText(label);
                    chipGroup.addView(chip);
                    chipTextVals.add(chip.getText().toString());
                }
                keyWords.setText("");
            }
        });
    }

    /**
     * Extracts the "Keyword" filter values from the chip group.
     */
    @Override
    public void getFilterInput() {
        // TODO: get the filter properties from fragment input
        KeywordFilter keywordFilter = new KeywordFilter(chipTextVals);
        filterCallback.onFilterApplied(keywordFilter);

        dismiss();
    }

    public void autoFillFilter(List<String> keyWordArray) {
        for (String label : keyWordArray) {
            Chip chip = new Chip(contentView.getContext());
            chip.setText(label);
            chipGroup.addView(chip);
            chipTextVals.add(chip.getText().toString());
        }
    }
}
