package com.utc2.facilityui.model;

public class RoomTypeItem {
    private String id;
    private String name;
    // Bạn có thể thêm các trường khác nếu cần, ví dụ description

    public RoomTypeItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoomTypeItem() {}

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
}