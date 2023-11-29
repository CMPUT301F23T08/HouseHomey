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

    public KeywordFilterFragment() {
        Log.d("filter", "filter");
    }

//    /**
//     * Constructs a new KeywordFilterFragment.
//     * @param title         The title of the "Keyword" filter dialog.
//     * @param contentView   The content view of the "Keyword" filter dialog.
//     * @param filterCallback The callback interface for handling filter changes.
//     */
//    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback) {
//        super(title, contentView, filterCallback);
//
//    }
//
//    /**
//     * Constructs a new KeywordFilterFragment.
//     * @param title         The title of the "Keyword" filter dialog.
//     * @param contentView   The content view of the "Keyword" filter dialog.
//     * @param filterCallback The callback interface for handling filter changes.
//     * @param keywordFilter The previous filter instance
//     */
//    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback, KeywordFilter keywordFilter) {
//        this(title, contentView, filterCallback);
//
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
        AlertDialog.Builder builder = createBuilder();

        Dialog dialog = builder.setTitle("Modify Date Filter").setView(contentView).create();

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
            this.filterCallback = getArguments().getSerializable("callback", FilterCallback.class);

            this.keywordFilter = getArguments().getSerializable("filter", KeywordFilter.class);
            if (this.keywordFilter != null)
                autoFillFilter(this.keywordFilter.getOgKeyWords());
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
            Context context = contentView.getContext();
            Chip chip = FragmentUtils.makeChip(label, true, chipGroup, context, R.color.white, R.color.brown, R.color.brown);
            chipTextVals.add(chip.getText().toString());
            chip.setOnCloseIconClickListener(v -> {
                chipGroup.removeView(chip);
                chipTextVals.remove(chip.getText().toString());
            });
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
