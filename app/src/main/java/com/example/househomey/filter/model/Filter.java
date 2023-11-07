package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.Objects;

/**
 * An abstract class representing a filter for a list of items.
 */
public abstract class Filter {

    /**
     * Applies the filter to a list of items and returns a new list containing only
     * the items that pass the filter.
     *
     * @param originalList The original list of items to be filtered.
     * @return A filtered list of items.
     */
    public abstract ArrayList<Item> filterList(ArrayList<Item> originalList);

    /**
     * Indicates whether some other object is "equal to" this filter.
     *
     * @param obj The reference object with which to compare.
     * @return {@code true} if this filter is the same as the obj argument; {@code false} otherwise.
     */
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

    /**
     * Returns a hash code value for the filter.
     *
     * @return A hash code value for this filter.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
