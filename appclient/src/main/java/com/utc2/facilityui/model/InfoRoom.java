package com.utc2.facilityui.model;

public class InfoRoom {
    private String name;
    private String Image;
    private String description;
    private String capacity;
    private String building;
    private String typeRoom;
    private String status;
    private String nameFManager;
    private String dateCreate;
    private String dateUpdate;

    public InfoRoom() {}

    public InfoRoom(String dateUpdate, String name, String image, String description, String capacity,
                    String building, String status, String typeRoom, String nameFManager, String dateCreate) {
        this.dateUpdate = dateUpdate;
        this.name = name;
        Image = image;
        this.description = description;
        this.capacity = capacity;
        this.building = building;
        this.status = status;
        this.typeRoom = typeRoom;
        this.nameFManager = nameFManager;
        this.dateCreate = dateCreate;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getTypeRoom() {
        return typeRoom;
    }

    public void setTypeRoom(String typeRoom) {
        this.typeRoom = typeRoom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNameFManager() {
        return nameFManager;
    }

    public void setNameFManager(String nameFManager) {
        this.nameFManager = nameFManager;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }
}
