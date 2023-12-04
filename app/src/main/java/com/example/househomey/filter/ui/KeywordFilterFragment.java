package com.example.househomey.filter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.KeywordFilter;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Fragment for applying "Keyword" filter criteria.
 * @author Antonio Lech Martin-Ozimek
 */
public class KeywordFilterFragment extends FilterFragment {
    private ChipGroup chipGroup;
    private ArrayList<String> chipTextVals = new ArrayList<>();
    protected KeywordFilter keywordFilter;
    Button addButton;
    EditText keyWords;

    /**
     * Creates dialog for keyword filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for keyword filter fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
        AlertDialog.Builder builder = createBuilder();

        Dialog dialog = builder.setTitle("Filter by Keywords").setView(contentView).create();

        chipGroup = contentView.findViewById(R.id.chip_group_labels);
        addButton = contentView.findViewById(R.id.add_keyword_button);
        keyWords = contentView.findViewById(R.id.keyword_edit_text);


        addButton.setOnClickListener(v -> {
            ArrayList<String> keyWordArray = new ArrayList<>(Arrays.asList(keyWords.getText().toString().split(" ")));
            keyWordArray.removeIf(e -> e.trim().isEmpty());
            autoFillFilter(keyWordArray);
            keyWords.setText("");
        });

        if (getArguments() != null) {
            filterCallback = getArguments().getSerializable("callback", FilterCallback.class);

            keywordFilter = getArguments().getSerializable("filter", KeywordFilter.class);
            if (keywordFilter != null)
                autoFillFilter(keywordFilter.getOgKeyWords());
        }

        return dialog;
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

    /**
     * Takes a list of keywords in and auto fills the chip group with them.
     * @param keyWordArray The list of keywords to fill the chip group with.
     */
    public void autoFillFilter(List<String> keyWordArray) {
        for (String label : keyWordArray) {
            String formattedLabel = FragmentUtils.initialCase(label);
            if (!chipTextVals.contains(formattedLabel)) {
                Context context = contentView.getContext();
                Chip chip = FragmentUtils.makeChip(formattedLabel, true, chipGroup, context, R.color.white, R.color.brown, R.color.brown);
                chipTextVals.add(chip.getText().toString());
                chip.setOnCloseIconClickListener(v -> {
                    chipGroup.removeView(chip);
                    chipTextVals.remove(chip.getText().toString());
                });
            }
        }
    }

    /**
     * Resets the list that was changed by the keyword filter.
     */
    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.keywordFilter);
        dismiss();
    }
}
