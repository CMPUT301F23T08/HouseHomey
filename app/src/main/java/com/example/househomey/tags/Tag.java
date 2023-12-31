package com.example.househomey.tags;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a tag object
 * @author Matthew Neufeld
 */
public class Tag implements Parcelable, Comparable<Tag> {
    private String tagLabel;
    private ArrayList<String> itemIds = new ArrayList<>();

    /**
     * Constructor for a tag object
     * @param tagLabel the name of the tag object
     * @param data the data from that document to initialize the instance
     */
    public Tag(String tagLabel, @NonNull Map<String, Object> data) {
        this.tagLabel = Objects.requireNonNull(tagLabel);
        if (data.containsKey("items") && data.get("items") instanceof List) {
            this.itemIds = new ArrayList<>((List<String>) Objects.requireNonNull(data.get("items")));
        }
    }

    /**
     * Sorts by alphabetical tag
     * @param o the object to be compared.
     * @return The compared objects
     */
    @Override
    public int compareTo(Tag o) {
        return this.getTagLabel().compareTo(o.getTagLabel());
    }

    /**
     * Getter for the tag label
     * @return tag label for the tag
     */
    public String getTagLabel() {
        return tagLabel;
    }

    /**
     * Getter for item ids
     * @return list of item ids with this tag
     */
    public ArrayList<String> getItemIds() {
        return itemIds;
    }

    //Creating the Parcelable CREATOR and
    //Implementing the Parcelable Interface below
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    /**
     * This method determines how an item object is written to a Parcel
     * @param out The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tagLabel);
        out.writeStringList(itemIds);
    }

    /**
     * Constructs a Tag object by reading its attributes from a Parcel object
     * @param in The parcel object that reads the data
     */
    protected Tag(Parcel in) {
        this.tagLabel = in.readString();
        this.itemIds = in.createStringArrayList();
    }

    /**
     * Interface method that provides the CONTENTS_FILE_DESCRIPTOR
     * for when a FileDescriptor object is to be put in a Parcelable
     * @return 0 since we don't use FileDescriptor objects in this project
     */
    @Override
    public int describeContents() {
        return 0;
    }

}
