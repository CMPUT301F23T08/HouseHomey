package com.example.househomey.tags;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a tag object
 * @author Matthew Neufeld
 */
public class Tag {
    private String tagLabel;
    private ArrayList<String> itemIds = new ArrayList<>();

    /**
     * Constructor for a tag object
     * @param tagLabel the name of the tag object
     * @param data the data from that document to initialize the instance
     */
    public Tag(String tagLabel, @NonNull Map<String, Object> data) {
        this.tagLabel = Objects.requireNonNull(tagLabel);
        if (data.containsKey("items")) {
            this.itemIds = new ArrayList<>((List<String>) Objects.requireNonNull(data.get("items")));
        }

    }

    /**
     * Getter for the tag label
     * @return tag label for the tag
     */
    public String getTagLabel() {
        return tagLabel;
    }

    public ArrayList<String> getItemIds() {
        return itemIds;
    }

}
