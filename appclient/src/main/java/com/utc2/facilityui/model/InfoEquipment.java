package com.utc2.facilityui.model;

public class InfoEquipment {
    private String name;
    private String Image;
    private String description;
    private String typeEquipment;
    private String status;
    private String nameFManager;
    private String dateCreate;
    private String dateUpdate;

    public InfoEquipment() {}

    public InfoEquipment(String name, String image, String description, String status,
                         String typeEquipment, String nameFManager, String dateCreate, String dateUpdate) {
        this.name = name;
        Image = image;
        this.description = description;
        this.status = status;
        this.typeEquipment = typeEquipment;
        this.nameFManager = nameFManager;
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeEquipment() {
        return typeEquipment;
    }

    public void setTypeEquipment(String typeEquipment) {
        this.typeEquipment = typeEquipment;
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
