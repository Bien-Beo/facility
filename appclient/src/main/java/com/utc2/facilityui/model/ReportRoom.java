package com.utc2.facilityui.model;
//
public class ReportRoom {
    String userID;
    String name;
    String description;

    public ReportRoom() {}

    public ReportRoom(String userID, String name, String description) {
        this.userID = userID;
        this.name = name;
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
}
