package com.utc2.facilityui.model;

public class Room {
    private String id;
    private String name;
    private String description;
    private String image;
    private int capacity;
    private String roomTypeName;

    public Room(String id, int capacity, String name, String roomTypeName, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.capacity = capacity;
        this.roomTypeName = roomTypeName;
    }

    public Room() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
}
