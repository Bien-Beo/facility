package com.utc2.facilityui.response;

import com.utc2.facilityui.model.Facility;

import java.util.List;
// Đ
public class RoomGroupResponse {
    private String type;
    private List<Facility> rooms; // này

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<Facility> getRooms() { return rooms; }
    public void setRooms(List<Facility> rooms) { this.rooms = rooms; }
}