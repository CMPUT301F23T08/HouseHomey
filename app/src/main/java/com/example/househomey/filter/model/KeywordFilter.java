package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A filter class that filters a list of items based on specific keywords.
 */
public class KeywordFilter extends Filter{
    ArrayList<String> keyWords;

    /**
     * Constructs a new KeywordFilter with the specified keywords.
     *
     * @param keyWords The keywords to filter by.
     */
    public KeywordFilter(ArrayList<String> keyWords) {this.keyWords = keyWords;}

    /**
     * Filters the passed in list of items, retaining only those items that match the
     * make value specified in this filter.
     *
     * @param itemList The original list of items to be filtered.
     * @return A filtered list of items that have at least one of the specified keywords.
     */
    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        return itemList.stream()
                .filter(item -> Arrays.stream(item.getDescription()
                        .split(" ")).anyMatch(keyWords::contains))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
