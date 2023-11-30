package com.example.househomey.sort;

import com.example.househomey.tags.Tag;

import java.util.Comparator;

public class TagComparator implements Comparator<Tag> {
    @Override
    public int compare(Tag tag1, Tag tag2) {
        return tag1.getTagLabel().compareTo(tag2.getTagLabel());
    }
}
