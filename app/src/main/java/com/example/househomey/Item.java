package com.example.househomey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Item {
    private String id;
    private String description;
    private Date acquisitionDate;
    private String make;
    private String model;
    private String serialNumber;
    private String comment;

    public Item(Map<String, Object> data) {
        if (data.containsKey("id")) {
            this.id = (String) data.get("id");
        }
        if (data.containsKey("description")) {
            this.description = (String) data.get("description");
        }
        if (data.containsKey("acquisitionDate")) {
            this.acquisitionDate = (Date) data.get("acquisitionDate");
        }
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
    }

    public HashMap<String, Object> data() {
        return new HashMap<>();
    }


}
