package com.example.househomey.filter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.KeywordFilter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


/**
 * Fragment for applying "Keyword" filter criteria.
 */
public class KeywordFilterFragment extends FilterFragment {
    private ChipGroup chipGroup = contentView.findViewById(R.id.chip_group_labels);
    private ArrayList<String> chipTextVals;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.err.println("Created");
        if (savedInstanceState != null) {
            chipTextVals = savedInstanceState.getStringArrayList("chipTextValues");
        } else {
            chipTextVals = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        System.err.println("Saved");
        savedInstanceState.putStringArrayList("chipTextValues", chipTextVals);
    }

    /**
     * Constructs a new KeywordFilterFragment.
     * @param title         The title of the "Keyword" filter dialog.
     * @param contentView   The content view of the "Keyword" filter dialog.
     * @param filterCallback The callback interface for handling filter changes.
     */
    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
        Button addButton = contentView.findViewById(R.id.add_keyword_button);
        EditText keyWords = contentView.findViewById(R.id.keyword_edit_text);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] keyWordArray = keyWords.getText().toString().split(" ");
                if (keyWordArray.length != 0) {
                    for (String label : keyWordArray) {
                        Chip chip = new Chip(contentView.getContext());
                        chip.setText(label);
                        chipGroup.addView(chip);
                        chipTextVals.add(chip.getText().toString());
                    }
                }

            }
        });
    }

    /**
     * Extracts the "Keyword" filter values from the chip group.
     */
    @Override
    public void getFilterInput() {
        // TODO: get the filter properties from fragment input
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View view = chipGroup.getChildAt(i);
            if (view instanceof Chip) {
                Chip chip = (Chip) view;
                chipTextVals.add(chip.getText().toString());
            }
        }

        KeywordFilter keywordFilter = new KeywordFilter(chipTextVals);
        filterCallback.onFilterApplied(keywordFilter);

        dismiss();
    }
}
