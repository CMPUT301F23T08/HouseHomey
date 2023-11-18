package com.example.househomey.filter.model;

import com.example.househomey.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A filter class that filters a list of items based on selected tags.
 * @author Jared Drueco
 */
public class TagFilter extends Filter {
    private Map<String, Boolean> tagSelectionMap;

    /**
     * Constructs a new TagFilter with the specified keywords.
     *
     * @param tagSelectionMap tags to filter by.
     */
    public TagFilter(Map tagSelectionMap) {
        this.tagSelectionMap = tagSelectionMap;
    }

    /**
     * Filters the passed in list of items, retaining only those items that match the
     * tags specified in this filter.
     *
     * @param itemList The original list of items to be filtered.
     * @return A filtered list of items that have at least one of the tags.
     */
    @Override
    public ArrayList<Item> filterList(ArrayList<Item> itemList) {
        if (tagSelectionMap.isEmpty()) return itemList;
        return itemList.stream()
                .filter(item ->
                        item.getTags().stream()
                                .map(String::toLowerCase)
                                .anyMatch(tagSelectionMap::get)
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }
}