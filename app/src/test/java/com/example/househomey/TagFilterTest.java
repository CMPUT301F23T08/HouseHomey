package com.example.househomey;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.example.househomey.filter.model.TagFilter;
import com.example.househomey.tags.Tag;
import com.google.firebase.Timestamp;
import static org.junit.Assert.assertEquals;

public class TagFilterTest {
    private ArrayList<Item> itemList;

    @Before
    public void setUp() {
        itemList = new ArrayList<>();
        itemList.add(createItem("3", "Smartphone", "Electronics", BigDecimal.valueOf(799.99), "Samsung", "Galaxy", "789012", "Nice phone"));
        itemList.add(createItem("1", "Laptop", "Electronics", BigDecimal.valueOf(999.99), "Dell", "XPS", "123456", "Good laptop"));
        itemList.add(createItem("2", "Sofa", "Furniture", BigDecimal.valueOf(499.99), "", "", "", ""));
    }

    private Item createItem(String id, String description, String tagLabel, BigDecimal cost, String make, String model, String serialNumber, String comment) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", description);
        data.put("acquisitionDate", new Timestamp(new Date()));
        data.put("cost", cost.toString());
        data.put("make", make);
        data.put("model", model);
        data.put("serialNumber", serialNumber);
        data.put("comment", comment);

        Item item = new Item(id, data);
        item.addTag(new Tag(tagLabel, data));
        return item;
    }

    @Test
    public void testNoTagSelected() {
        Set<Tag> selectedTags = new HashSet<>();
        TagFilter tagFilter = new TagFilter(selectedTags);
        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(3, filteredList.size());
    }

    @Test
    public void testOneTagWithOneItem() {
        Set<Tag> selectedTags = new HashSet<>();
        Map<String, Object> tagData = new HashMap<>();
        tagData.put("items", new ArrayList<>(Collections.singletonList("2")));
        selectedTags.add(new Tag("Electronics", tagData));
        TagFilter tagFilter = new TagFilter(selectedTags);
        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(1, filteredList.size());
    }

    @Test
    public void testOneTagWithMultipleItems() {
        Set<Tag> selectedTags = new HashSet<>();
        Map<String, Object> tagData = new HashMap<>();
        tagData.put("items", new ArrayList<>(Arrays.asList("3", "1")));
        selectedTags.add(new Tag("Electronics", tagData));
        TagFilter tagFilter = new TagFilter(selectedTags);
        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(2, filteredList.size());
    }

    @Test
    public void testAllTags() {
        Set<Tag> selectedTags = new HashSet<>();
        Map<String, Object> electronicTagData = new HashMap<>();
        Map<String, Object> furnitureData = new HashMap<>();
        electronicTagData.put("items", new ArrayList<>(Arrays.asList("3", "1")));
        furnitureData.put("items", new ArrayList<>(Collections.singletonList("2")));

        selectedTags.add(new Tag("Electronics", electronicTagData));
        selectedTags.add(new Tag("Furniture", furnitureData));

        TagFilter tagFilter = new TagFilter(selectedTags);
        ArrayList<Item> filteredList = tagFilter.filterList(itemList);
        assertEquals(3, filteredList.size());
    }
}
