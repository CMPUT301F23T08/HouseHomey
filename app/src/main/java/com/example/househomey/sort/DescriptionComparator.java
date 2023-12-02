package com.example.househomey.sort;

import com.example.househomey.item.Item;

import java.util.Comparator;

/**
 * Comparator for the an Item that compares items based on their Description Alphabetically
 * @author Sami Jagirdar
 */
public class DescriptionComparator implements Comparator<Item> {
    /**
     * Compares 2 items by comparing their descriptions alphabetically
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1 if o1's description alphabetically comes before o2's, 0 if they have the same description,
     * -1 otherwise
     */
    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getDescription().toLowerCase().compareTo(o2.getDescription().toLowerCase());
    }
}
