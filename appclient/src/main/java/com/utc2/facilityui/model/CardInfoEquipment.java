package com.utc2.facilityui.model;
//
public class CardInfoEquipment {
    private String id;
    private String nameEquip;
    private String imgSrc;
    private String roleManager;
    private String nameManager;

    public CardInfoEquipment() {}

    // Constructor có thể cần cập nhật nếu dùng
    public CardInfoEquipment(String id, String imgSrc, String nameEquip, String roleManager, String nameManager) {
        this.id = id;
        this.imgSrc = imgSrc;
        this.nameEquip = nameEquip;
        this.roleManager = roleManager;
        this.nameManager = nameManager;
    }

    // --- Getters and Setters ---
    public String getId() { return id; } // Getter cho ID
    public void setId(String id) { this.id = id; } // Setter cho ID

    public String getImgSrc() { return imgSrc; }
    public void setImgSrc(String imgSrc) { this.imgSrc = imgSrc; }

    // Sửa lại getter/setter cho khớp tên biến 'nameEquip'
    public String getNameEquip() { return nameEquip; }
    public void setNameEquip(String nameEquip) { this.nameEquip = nameEquip; }

    public String getRoleManager() { return roleManager; }
    public void setRoleManager(String roleManager) { this.roleManager = roleManager; }
    public String getNameManager() { return nameManager; }
    public void setNameManager(String nameManager) { this.nameManager = nameManager; }
}