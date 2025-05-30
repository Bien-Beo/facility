package com.utc2.facilityui.model;

public class BuildingItem {
    private String id;
    private String name;

    // Constructor, getters, setters
    public BuildingItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public BuildingItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString() rất hữu ích nếu bạn không dùng StringConverter phức tạp,
    // nhưng dùng StringConverter vẫn được khuyến nghị để linh hoạt hơn.
    // @Override
    // public String toString() {
    //     return name;
    // }
}