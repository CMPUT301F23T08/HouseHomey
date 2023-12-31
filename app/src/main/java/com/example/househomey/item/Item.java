package com.example.househomey.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.househomey.tags.Tag;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents an inventory item with a variety of properties
 * @author Lukas Bonkowski, Matthew Neufeld, Owen Cooke, Sami Jagirdar
 * @see ItemAdapter
 */
public class Item implements Serializable, Parcelable {
    private String id;
    private String description;
    private Date acquisitionDate;
    private String make = "";
    private String model = "";
    private String serialNumber = "";
    private String comment = "";
    private BigDecimal cost;
    private List<String> photoIds = new ArrayList<>();
    private Set<Tag> tags = new TreeSet<>();
    private boolean checked = false;

    /**
     * This constructs a new item from a Map of data with a reference to its Firestore document
     *
     * @param id   The id of this object's document in the firestore database
     * @param data The data from that document to initialize the instance
     * @throws NullPointerException if a null required field is given
     */
    public Item(String id, @NonNull Map<String, Object> data) {
        // Required fields, will throw exceptions
        this.id = Objects.requireNonNull(id);
        this.description = (String) Objects.requireNonNull(data.get("description"));
        this.acquisitionDate = ((Timestamp) Objects.requireNonNull(data.get("acquisitionDate"))).toDate();
        this.cost = new BigDecimal((String) Objects.requireNonNull(data.get("cost"))).setScale(2, RoundingMode.HALF_UP);

        // Optional fields
        if (data.containsKey("make")) {
            this.make = (String) data.get("make");
        }
        if (data.containsKey("model")) {
            this.model = (String) data.get("model");
        }
        if (data.containsKey("serialNumber")) {
            this.serialNumber = (String) data.get("serialNumber");
        }
        if (data.containsKey("comment")) {
            this.comment = (String) data.get("comment");
        }
        if (data.containsKey("photoIds")) {
            this.photoIds = new ArrayList<>((List<String>) data.get("photoIds"));
        }
    }

    /**
     * This constructs a new item from a Map of data with a reference to its Firestore document
     *
     * @param id   The id of this object's document in the firestore database
     * @param data The data from that document to initialize the instance
     * @param tagRef The tag collection
     * @param listener The listener for completion of tag intialization
     * @throws NullPointerException if a null required field is given
     */
    public Item(String id, @NonNull Map<String, Object> data, CollectionReference tagRef, OnItemInitializedListener listener) {
        this(id, data);
        initTags(tagRef, listener);
    }

    /**
     * Listens for completion of the item's tag initialization
     */
    public interface OnItemInitializedListener {
        void onItemInitialized(Item item);
    }


    /**
     * @param tagRef the collection of tags
     * @param listener The listener to notify when tag intialization is complete
     */
    private void initTags(CollectionReference tagRef, OnItemInitializedListener listener) {
        tagRef.whereArrayContains("items", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tag tag = new Tag(document.getId(), document.getData());
                            tags.add(tag);
                        }
                        Log.i("Item Tag", tags.toString());
                    } else {
                        Log.e("Item Tag", "Couldn't set item tag");
                    }
                    listener.onItemInitialized(this);
                });
    }

    /**
     * Adds new tag to tag set
     * @param tag new tag to add
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Clears the tags in the set
     */
    public void clearTags() {
        tags.clear();
    }

    /**
     * Getter for tags
     * @return set of tags
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Setter for tags
     * @param tags new set of tags
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Convenient getter for map of properties to be used for Firestore queries
     *
     * @return A Map containing all the properties of this object as key-value pairs
     */
    public Map<String, Object> getData() {
        Map<String, Object> itemData = new HashMap<>();

        // Add required properties
        itemData.put("description", description);
        itemData.put("acquisitionDate", new Timestamp(acquisitionDate));
        itemData.put("cost", cost.toString());

        // Only add optional properties if they have non-empty values
        if (!make.isEmpty()) {
            itemData.put("make", make);
        }
        if (!model.isEmpty()) {
            itemData.put("model", model);
        }
        if (!serialNumber.isEmpty()) {
            itemData.put("serialNumber", serialNumber);
        }
        if (!comment.isEmpty()) {
            itemData.put("comment", comment);
        }
        if (!photoIds.isEmpty()) {
            itemData.put("photoIds", photoIds);
        }
        return itemData;
    }

    /**
     * Getter for checked
     *
     * @return The checked state of this item
     */
    public boolean getChecked() {
        return checked;
    }

    /**
     * Setter for checked
     * @param newChecked The new value for checked
     */
    public void setChecked(boolean newChecked) {
        checked = newChecked;
    }

    /**
     * Getter for id
     *
     * @return The id of this item in firestore
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for acquisitionDate
     *
     * @return The acquisition date of this item
     */
    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Getter for description
     *
     * @return The brief description of this item
     */
    public String getDescription() {
        return description;
    }


    /**
     * Getter for cost
     *
     * @return The cost of this item
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Getter for make
     *
     * @return make of the item
     */
    public String getMake() {
        return make;
    }

    /**
     * Getter for model
     *
     * @return model of the item
     */
    public String getModel() {
        return model;
    }

    /**
     * Getter for serial number
     *
     * @return serial number of the item
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Getter for comment
     *
     * @return comment for the item
     */
    public String getComment() {
        return comment;
    }

    /**
     * Getter for photoIds
     *
     * @return List of Cloud Storage photo IDs associated with this Item
     */
    public List<String> getPhotoIds() {
        return photoIds;
    }

    /**
     * Setter for photoIds
     *
     * @param photoIds List of Cloud Storage photo IDs to set for this Item
     */
    public void setPhotoIds(List<String> photoIds) {
        this.photoIds = photoIds;
    }

    //Creating the Parcelable CREATOR and
    //Implementing the Parcelable Interface below
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }
        public Item[] newArray(int size) {
            return new Item[size];
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
        out.writeString(id);
        out.writeString(description);
        out.writeLong(acquisitionDate.getTime());
        out.writeString(make); //Note: You can pass null values to Parcel
        out.writeString(model);
        out.writeString(serialNumber);
        out.writeString(comment);
        out.writeSerializable(cost.toString());
        out.writeList(photoIds);
        out.writeList(new ArrayList<>(tags));
    }

    /**
     * Constructs an item object by reading its attributes from a Parcel object
     * @param in The parcel object that reads the data
     */
    protected Item(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.acquisitionDate = new Date(in.readLong());
        this.make = in.readString();
        this.model = in.readString();
        this.serialNumber = in.readString();
        this.comment = in.readString();
        this.cost = new BigDecimal(in.readString()).setScale(2, RoundingMode.HALF_UP);
        in.readStringList(this.photoIds);
        this.tags = new TreeSet<>();
        List<Tag> tagList = new ArrayList<>();
        in.readList(tagList, Tag.class.getClassLoader(), Tag.class);
        this.tags.addAll(tagList);
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