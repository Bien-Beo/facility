package com.utc2.facilityui.response;

/**
 * Đại diện cho cấu trúc phản hồi API chuẩn khi kết quả trả về là một đối tượng đơn lẻ (không phải danh sách).
 * Ví dụ: khi approve/reject/get một booking cụ thể.
 * @param <T> Kiểu dữ liệu của đối tượng kết quả (ví dụ: BookingResponse)
 */
public class ApiSingleResponse<T> {
    private int code;
    private String message;  // Thông điệp tùy chọn từ backend (ví dụ: "Success", "Booking not found")
    private T result;

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}