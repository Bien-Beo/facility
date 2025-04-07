package com.utc2.facility.exception;

import lombok.Getter; // Thêm @Getter cho tiện

@Getter // Dùng @Getter thay cho việc viết getter/setter thủ công
public class AppException extends RuntimeException {

    private final ErrorCode errorCode; // Nên là final nếu không thay đổi sau khi tạo

    // --- Constructor cũ (Giữ lại để dùng khi message mặc định là đủ) ---
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // Gọi super với message mặc định
        this.errorCode = errorCode;
    }

    // --- Constructor MỚI cho phép truyền message tùy chỉnh ---
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage); // Gọi super với message tùy chỉnh được truyền vào
        this.errorCode = errorCode;
    }

    // --- Constructor MỚI cho phép truyền message tùy chỉnh VÀ nguyên nhân (cause) ---
    public AppException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause); // Gọi super với message tùy chỉnh và nguyên nhân gốc
        this.errorCode = errorCode;
    }

    // --- Constructor MỚI chỉ truyền ErrorCode và cause (dùng message mặc định) ---
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause); // Gọi super với message mặc định và nguyên nhân gốc
        this.errorCode = errorCode;
    }

    // Không cần setter nếu errorCode là final
    // public void setErrorCode(ErrorCode errorCode) {
    //     this.errorCode = errorCode;
    // }
}