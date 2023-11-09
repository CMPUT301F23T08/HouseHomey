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
     * Constructs new DateFilter with given start and end dates
     * @param startDate the lower bound of the filter
     * @param endDate the upper bound of the filter
     */
    public DateFilter(Date startDate, Date endDate) {
        this.startDate = startDate;
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
        return itemList.stream()
                .filter(item -> {
                    Date itemDate = item.getAcquisitionDate();
                    // only return non-null items after startDate and before endDate
                    return itemDate != null && itemDate.after(startDate) && itemDate.before(endDate);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
