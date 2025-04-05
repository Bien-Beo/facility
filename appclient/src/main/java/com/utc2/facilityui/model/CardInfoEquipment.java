package com.utc2.facilityui.model;

public class CardInfoEquipment {
    private String nameEquip;
    private String imgSrc;
    private String roleManager;
    private String nameManager;

    public CardInfoEquipment() {}

    public CardInfoEquipment(String imgSrc, String nameCard, String roleManager, String nameManager) {
        this.imgSrc = imgSrc;
        this.nameEquip = nameCard;
        this.roleManager = roleManager;
        this.nameManager = nameManager;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getNameCard() {
        return nameEquip;
    }

    public void setNameCard(String nameCard) {
        this.nameEquip = nameCard;
    }

    public String getRoleManager() {
        return roleManager;
    }

    public void setRoleManager(String roleManager) {
        this.roleManager = roleManager;
    }

    public String getNameManager() {
        return nameManager;
    }

    public void setNameManager(String nameManager) {
        this.nameManager = nameManager;
    }
}
