package com.utc2.facilityui.response; // Or your correct package

// Represents the overall API response structure for paged data
public class ApiPageResponse<T> {
    private int code;
    private String message; // Optional message field
    private Page<T> result; // Holds the Page object

    // Getters and Setters...
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Page<T> getResult() { return result; }
    public void setResult(Page<T> result) { this.result = result; }
}

// --- Keep ApiSingleResponse if needed for other calls ---
// package com.utc2.facilityui.response;
// public class ApiSingleResponse<T> {
//     private int code;
//     private String message;
//     private T result;
//     // Getters and Setters...
// }

// --- Keep ApiErrorResponse if needed for error parsing ---
// package com.utc2.facilityui.response;
// public class ApiErrorResponse {
//     private int code;
//     private String message;
//     // Getters and Setters...
// }