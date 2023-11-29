package com.example.househomey.tags;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tag {
    private String tagLabel;
    private ArrayList<String> itemIds = new ArrayList<>();

    public Tag(String tagLabel, @NonNull Map<String, Object> data) {
        // Required fields, will throw exceptions
        this.tagLabel = Objects.requireNonNull(tagLabel);

        if (data.containsKey("items")) {
            this.itemIds = new ArrayList<>((List<String>) data.get("items"));
        }

    }

    public String getTagLabel() {
        return tagLabel;
    }


}
