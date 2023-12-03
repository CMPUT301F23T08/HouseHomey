package com.example.househomey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import com.example.househomey.tags.Tag;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TagTest {

    @Test
    public void testConstructor() {
        String tagLabel = "TestTag";
        Map<String, Object> data = new HashMap<>();
        data.put("items", null);

        Tag tag = new Tag(tagLabel, data);

        assertEquals(tagLabel, tag.getTagLabel());
        assertTrue(tag.getItemIds().isEmpty());
    }

    @Test
    public void testCompareTo() {
        Tag tag1 = new Tag("Books", new HashMap<>());
        Tag tag2 = new Tag("Instruments", new HashMap<>());

        int result = tag1.compareTo(tag2);

        assertTrue(result < 0); // Expecting Books to come before Instruments alphabetically
    }

    @Test
    public void testDescribeContents() {
        Tag tag = new Tag("TestTag", new HashMap<>());
        assertEquals(0, tag.describeContents());
    }
}
