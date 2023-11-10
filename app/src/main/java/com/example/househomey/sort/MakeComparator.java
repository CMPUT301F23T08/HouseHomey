package com.example.househomey.sort;

import com.example.househomey.Item;

import java.util.Comparator;

/**
 * Comparator for the an Item that compares items based on their Make alphabetically
 * @author Sami Jagirdar
 */
public class MakeComparator implements Comparator<Item> {
    /**
     * Compares 2 Items by comparing their Makes alphabetically
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1 if o1's make alphabetically comes before o2's, 0 if their makes are the same,
     * -1 otherwise
     */
    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getMake().toLowerCase().compareTo(o2.getMake().toLowerCase());
    }
}
