package com.utc2.facilityui.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.utc2.facilityui.model.Result;
import com.utc2.facilityui.model.User;

public class ApiResponse<T> {
//    @Expose
//    @SerializedName("code")
//    private int code;
//
//    @Expose
//    @SerializedName("result")
//    private T result;
//
//    public int getCode() { return code; }
//    public T getResult() { return result; }
    private int code;
    private Result<T> result;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public Result<T> getResult() { return result; }
    public void setResult(Result<T> result) { this.result = result; }
}
