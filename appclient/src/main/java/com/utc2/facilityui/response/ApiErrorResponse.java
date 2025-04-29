package com.utc2.facilityui.response;

/**
 * Lớp đại diện cho cấu trúc JSON của một response lỗi trả về từ API.
 */
public class ApiErrorResponse {
    private int code;
    private String message;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() { // <<< Phương thức getMessage() đã có ở đây
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}