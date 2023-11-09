package com.example.househomey.filter.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.KeywordFilter;
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
        autoFillFilter(this.keywordFilter.getOgKeyWords());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> keyWordArray = Arrays.asList(keyWords.getText().toString().split(" "));
                keyWordArray = removeEmptyStrings(keyWordArray);
                autoFillFilter(keyWordArray);
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
                keyWordArray = removeEmptyStrings(keyWordArray);
                autoFillFilter(keyWordArray);
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
            Context context = contentView.getContext();
            Chip chip = new Chip(context);
            chip.setText(label);
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColorResource(R.color.white);
            chip.setChipStrokeColorResource(R.color.brown);
            chip.setTextColor(ContextCompat.getColor(context , R.color.brown));
            chipGroup.addView(chip);
            chipTextVals.add(chip.getText().toString());
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                    chipTextVals.remove(chip.getText().toString());
                }
            });
        }
    }

    private List<String> removeEmptyStrings(List<String> listToFilter) {
        List<String> nonEmptyKeyWords = new ArrayList<>();

        for (String keyword : listToFilter) {
            if (!keyword.trim().isEmpty()) { // Check if the keyword is not empty after trimming whitespace
                nonEmptyKeyWords.add(keyword);
            }
        }

        return nonEmptyKeyWords;
    }

    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.keywordFilter);
        dismiss();
    }
}
