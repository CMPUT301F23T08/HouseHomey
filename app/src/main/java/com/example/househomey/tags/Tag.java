package com.example.househomey.tags;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tag {
    private String id;
    private String tagLabel;

    public Tag(String tagLabel) {
        this.tagLabel = tagLabel;
    }
    /**
     * Getter for id
     *
     * @return The id of this tag in firestore
     */
    public String getId() {
        return id;
    }

}