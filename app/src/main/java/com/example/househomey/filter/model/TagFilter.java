package com.example.househomey.filter.model;

import com.example.househomey.item.Item;
import com.example.househomey.tags.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A filter class that filters a list of items based on selected tags.
 * @author Jared Drueco
 */
public class TagFilter extends Filter implements Serializable {
    public Set<Tag> selectedTags;

    /**
     * Constructs a new TagFilter with the specified keywords.
     *
     * @param selectedTags tags to filter by.
     */
    public TagFilter(Set selectedTags) {
        this.selectedTags = selectedTags;
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
        if (selectedTags.isEmpty()) return itemList;

        // Collect all item IDs corresponding to selected tags
        Set<String> selectedTagItemIds = selectedTags.stream()
                .flatMap(tag -> tag.getItemIds().stream())
                .collect(Collectors.toSet());
        // Filter items based on selected item IDs
        return itemList.stream()
                .filter(item -> selectedTagItemIds.contains(item.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}