package com.utc2.facilityui.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.utc2.facilityui.model.User;
/// /
public class ApiResponse<T> {
    @Expose
    @SerializedName("code")
    private int code;

    @Expose
    @SerializedName("result")
    private T result;

    public int getCode() { return code; }
    public T getResult() { return result; }
}

