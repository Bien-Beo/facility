package com.utc2.facility.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1004, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    NOT_NULL_EXCEPTION(1006, "Not null", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You don't have permission", HttpStatus.FORBIDDEN),
    ROOM_EXISTED(1009, "Room existed", HttpStatus.CONFLICT),
    ROOM_TYPE_NOT_FOUND(1010, "Room type not found", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND (1011, "Room not found", HttpStatus.NOT_FOUND),
    ROOM_ALREADY_BOOKED(1012, "Room is already booked", HttpStatus.BAD_REQUEST),
    ROOM_UNAVAILABLE(1013, "Room is unavailable", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1013, "Role not found", HttpStatus.NOT_FOUND),
    BUILDING_NOT_FOUND(1014, "Building not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_TYPE_NOT_FOUND(1015, "Equipment type not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_NOT_FOUND(1016, "Equipment not found", HttpStatus.NOT_FOUND),
    BORROW_REQUEST_NOT_FOUND(1017, "Borrow request not found", HttpStatus.NOT_FOUND),
    BORROW_TIME_INVALID(1018, "Borrow time invalid", HttpStatus.BAD_REQUEST),
    BORROW_REQUEST_EXPIRED(1019, "Borrow request expired", HttpStatus.BAD_REQUEST),
    BORROW_RETURN_TIME_INVALID(1020, "Borrow return time invalid", HttpStatus.BAD_REQUEST),
    REQUEST_ALREADY_PROCESSED(1017, "Request already processed", HttpStatus.BAD_REQUEST),
    REQUEST_NOT_APPROVED(1018, "Request not approved", HttpStatus.BAD_REQUEST),
    BORROW_EQUIPMENT_NOT_FOUND(1018, "Borrow equipment not found", HttpStatus.NOT_FOUND),
    CANCEL_REQUEST_NOT_FOUND(1019, "Cancel request not found", HttpStatus.NOT_FOUND),
    REPAIR_REQUEST_NOT_FOUND(1020, "Repair request not found", HttpStatus.NOT_FOUND),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}