package com.utc2.facilityui.response; // Hoặc response package

import java.util.List;
//as
// Đại diện cho toàn bộ response từ API /dashboard/room
public class DashboardRoomApiResponse {
    private int code;
    private List<RoomGroupResponse> result; // <<< Danh sách các nhóm phòng

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public List<RoomGroupResponse> getResult() { return result; }
    public void setResult(List<RoomGroupResponse> result) { this.result = result; }
}