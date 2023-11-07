package com.example.househomey.Filters;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Filter {

    /**
     * Applies the filter to a list of items and returns a new list containing only
     * the items that pass the filter.
     *
     * @param originalList The original list of items to be filtered.
     * @return A filtered list of items.
     */
    public abstract ArrayList<Item> filterList(ArrayList<Item> originalList);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
