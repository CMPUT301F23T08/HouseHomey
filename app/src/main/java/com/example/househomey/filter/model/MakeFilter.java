package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A filter class that filters a list of items based on a specific make value.
 */
public class MakeFilter extends Filter {
    public String makeToFilterBy;

    /**
     * Constructs a new MakeFilter with the specified make value.
     *
     * @param make The make value to filter by.
     */
    public MakeFilter(String make) {
        this.makeToFilterBy = make;
    }

    /**
     * Filters the passed in list of items, retaining only those items that match the
     * make value specified in this filter.
     *
     * @param itemList The list of items to be filtered.
     * @return A filtered list of items that have the specified make value.
     */
    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        if (makeToFilterBy == null || makeToFilterBy.isEmpty()) {
            return itemList;
        }
        return itemList.stream()
                .filter(item -> item.getMake().equalsIgnoreCase(makeToFilterBy))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}