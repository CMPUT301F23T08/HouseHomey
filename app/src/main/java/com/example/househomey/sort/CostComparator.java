package com.example.househomey.sort;

import com.example.househomey.Item;

import org.checkerframework.checker.units.qual.C;

import java.util.Comparator;

/**
 * Comparator for the an Item that compares items based on their costs
 * @author Sami Jagirdar
 */
public class CostComparator implements Comparator<Item> {

    /**
     * Compare 2 items from their costs
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1 if o1's cost is less than o2's, 0 if equal, -1 otherwise
     */
    @Override
    public int compare(Item o1, Item o2) {
        return  o1.getCost().compareTo(o2.getCost());
    }
}
