package com.example.househomey.filter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.R;
import com.example.househomey.filter.model.FilterCallback;
import com.example.househomey.filter.model.DateFilter;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Fragment for applying the date filter
 * @author Matthew Neufeld
 */
public class DateFilterFragment extends FilterFragment {

    private TextInputEditText startDateTextView;
    private TextInputEditText endDateTextView;
    private Date startDate;
    private Date endDate;

    public DateFilterFragment(String title, View contentView, FilterCallback filterCallback) {
        super(title, contentView, filterCallback);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog builder = super.onCreateDialog(savedInstanceState);

        startDateTextView = getDateTextView(contentView, R.id.start_date_filter, R.id.start_date_layout);
        endDateTextView = getDateTextView(contentView, R.id.end_date_filter, R.id.end_date_layout);

        return builder;

    }

    private TextInputEditText getDateTextView(View contentView, int dateViewId, int layoutId) {

        TextInputEditText dateTextView = contentView.findViewById(dateViewId);

        MaterialDatePicker<Long> datePicker = FragmentUtils.createDatePicker();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            dateTextView.setText(dateFormat.format(date));
            ((TextInputLayout) contentView.findViewById(layoutId)).setError(null);
            setStartOrEndDate(dateTextView, date);
        });

        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
        return dateTextView;
    }

    private void setStartOrEndDate(EditText dateTextView, Date date) {
        if (dateTextView.getId() == R.id.start_date_filter) {
            startDate = date;
        } else if (dateTextView.getId() == R.id.end_date_filter) {
            endDate = date;
        }
    }

    @Override
    public void getFilterInput() {
        DateFilter dateFilter = new DateFilter(startDate, endDate);
        filterCallback.onFilterApplied(dateFilter);
        dismiss();
    }
}
