package com.utc2.facilityui.response;

public class ApiContainerForPagedData<T> { // T sẽ là BookingResponse
    private int code;
    private String message; // Thêm nếu JSON có
    private ResultWithNestedPage<T> result;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public ResultWithNestedPage<T> getResult() { return result; }
    public void setResult(ResultWithNestedPage<T> result) { this.result = result; }
}