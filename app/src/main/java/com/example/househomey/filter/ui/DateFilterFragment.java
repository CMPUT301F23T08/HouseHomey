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
import com.example.househomey.filter.model.KeywordFilter;
import com.example.househomey.utils.FragmentUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * Fragment for applying the date filter
 * @author Matthew Neufeld
 */
public class DateFilterFragment extends FilterFragment {
    protected DateFilter dateFilter;

    /**
     * Creates dialog for date filter fragment
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return dialog object for date filter fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_dates, null);
        setDateTextView(contentView, R.id.start_date_filter, R.id.start_date_layout);
        setDateTextView(contentView, R.id.end_date_filter, R.id.end_date_layout);

        Dialog dialog = createBuilder().setTitle("Filter by Date").setView(contentView).create();

        filterCallback = getArguments().getSerializable("callback", FilterCallback.class);

        dateFilter = getArguments().getSerializable("filter", DateFilter.class);
        if (dateFilter != null)
            autoFillLastFilter(dateFilter);
        else
            dateFilter = new DateFilter();
        return dialog;
    }

    /**
     * sets dateTextView based on datePicker and whether user has picked start or end date
     * @param contentView content view of date filter fragment
     * @param dateViewId id of the date text view
     * @param layoutId id of layout associated with  date text view
     * @return TextInputEditText for the date selected
     */
    private void setDateTextView(View contentView, int dateViewId, int layoutId) {

        TextInputEditText dateTextView = contentView.findViewById(dateViewId);

        MaterialDatePicker<Long> datePicker = FragmentUtils.createDatePicker();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            dateTextView.setText(FragmentUtils.formatDate(date));
            ((TextInputLayout) contentView.findViewById(layoutId)).setError(null);
            setStartOrEndDate(dateTextView, date);
        });

        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
    }

    /**
     * sets the start or end date depending on which text view user is choosing
     * @param dateTextView text view for selected date
     * @param date user selected date
     */
    private void setStartOrEndDate(EditText dateTextView, Date date) {
        if (dateTextView.getId() == R.id.start_date_filter) {
            dateFilter.setStartDate(date);
        } else if (dateTextView.getId() == R.id.end_date_filter) {
            dateFilter.setEndDate(date);
        }
    }

    /**
     * Populates start and end date fields with the dateFilter previously applied
     * @param dateFilter the previous dateFilter used
     */
    public void autoFillLastFilter(DateFilter dateFilter) {
        EditText startDateEditText = contentView.findViewById(R.id.start_date_filter);
        EditText endDateEditText = contentView.findViewById(R.id.end_date_filter);

        if (dateFilter.getStartDate() != null)
            startDateEditText.setText(FragmentUtils.formatDate(dateFilter.getStartDate()));
        if (dateFilter.getEndDate() != null)
            endDateEditText.setText(FragmentUtils.formatDate(dateFilter.getEndDate()));
    }

    /**
     * Gets the filter input (start and end date) and creates callback on the DateFilter
     */
    @Override
    public void getFilterInput() {
        filterCallback.onFilterApplied(dateFilter);
        dismiss();
    }

    /**
     * Deletes the currently applied "Date" filter on the home fragment.
     */
    @Override
    public void resetFilter() {
        filterCallback.onFilterReset(this.dateFilter);
        dismiss();
    }
}
