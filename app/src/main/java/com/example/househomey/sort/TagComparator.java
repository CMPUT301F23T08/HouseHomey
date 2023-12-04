package com.example.househomey.sort;

import com.example.househomey.item.Item;
import com.example.househomey.tags.Tag;

import java.util.Comparator;
import java.util.Set;

public class TagComparator implements Comparator<Item> {
    /**
     * Compares the first tag of each item
     * @param item1 the first object to be compared.
     * @param item2 the second object to be compared.
     * @return 1 if o1 is o2, 0 if tied, -1 otherwise
     */
    @Override
    public int compare(Item item1, Item item2) {
        Tag tag1 = getFirstTag(item1);
        Tag tag2 = getFirstTag(item2);

        if (tag1 == null && tag2 == null) {
            return 0;
        } else if (tag1 == null) {
            return 1;
        } else if (tag2 == null) {
            return -1;
        } else {
            return tag1.compareTo(tag2);
        }
    }

    private Tag getFirstTag(Item item) {
        Set<Tag> tags = item.getTags();
        if (tags.isEmpty()) {
            return null;
        } else {
            return tags.iterator().next();
        }
    }
}
