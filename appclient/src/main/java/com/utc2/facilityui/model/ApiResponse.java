package com.utc2.facilityui.model;
//
// import javax.xml.transform.Result; // <--- XÓA DÒNG NÀY ĐI
import java.util.List; // Import này vẫn cần thiết nếu bạn dùng List đâu đó (mặc dù không thấy trong đoạn code này)

// Lớp generic để chứa response chung từ API
public class ApiResponse<T> {
    private int code;
    // Bây giờ trình biên dịch sẽ hiểu 'Result' là lớp Result trong cùng package com.utc2.facilityui.model
    private Result<T> result;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public Result<T> getResult() { return result; }
    public void setResult(Result<T> result) { this.result = result; }
}