package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Filters out items from list that are after the given start date and before the given end date.
 * @author Matthew Neufeld
 */
public class DateFilter extends Filter {
    private Date startDate;
    private Date endDate;

    /**
     * Gets the start date of the filter.
     * @return The start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the startDate
     * @param startDate new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the filter
     * @return end date.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the endDate
     * @param endDate new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Filters the passed in list of items, retains only items that are within the bounds of the
     * start and end dates.
     * @param itemList The original list of items to be filtered.
     * @return filtered list of items that are within the start and end date
     */
    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        if (startDate == null && endDate == null) return itemList;
        return itemList.stream()
                .filter(item -> {
                    Date itemDate = item.getAcquisitionDate();
                    // only return non-null items after startDate and before endDate
                    boolean result = itemDate != null;
                    if (startDate != null)
                        result = result && itemDate.compareTo(startDate) >= 0;
                    if (endDate != null)
                        result = result && itemDate.compareTo(endDate) <= 0;
                    return result;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
