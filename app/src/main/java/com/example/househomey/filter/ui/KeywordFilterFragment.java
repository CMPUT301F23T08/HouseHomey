package com.example.househomey.filter.ui;

import android.view.View;

import com.example.househomey.filter.model.FilterCallback;

public class KeywordFilterFragment extends FilterFragment {

    public KeywordFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    @Override
    public void getFilterInput() {}
    public void resetFilter() {}
}
