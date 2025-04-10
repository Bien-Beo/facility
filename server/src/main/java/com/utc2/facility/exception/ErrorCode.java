package com.utc2.facility.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN(1030, "Username or password not existed", HttpStatus.NOT_FOUND),
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
    EQUIPMENT_ITEM_NOT_FOUND(1021, "Equipment item not found", HttpStatus.NOT_FOUND),
    MAINTENANCE_TICKET_NOT_FOUND(1022, "Maintenance ticket not found", HttpStatus.NOT_FOUND),
    BOTH_ROOM_AND_ITEM_PROVIDED(1023, "Both room and item provided", HttpStatus.BAD_REQUEST),
    ROOM_OR_ITEM_REQUIRED(1024, "Room or item required", HttpStatus.BAD_REQUEST),
    EQUIPMENT_UNAVAILABLE(1025, "Equipment unavailable", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(1026, "Booking not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_UPDATABLE(1027, "Booking not updatable", HttpStatus.BAD_REQUEST),
    BOOKING_STATUS_INVALID(1028, "Booking status invalid", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT(1029, "Invalid date format", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1030, "Invalid input", HttpStatus.BAD_REQUEST),
    MODEL_NOT_FOUND(1031, "Model not found", HttpStatus.NOT_FOUND),
    SERIAL_NUMBER_EXISTED(1032, "Serial number existed", HttpStatus.CONFLICT),
    ASSET_TAG_EXISTED(1033, "Asset tag existed", HttpStatus.CONFLICT),
    EMAIL_EXISTED(1034, "Email existed", HttpStatus.CONFLICT),
    FORBIDDEN(1035, "Forbidden", HttpStatus.FORBIDDEN),
    BOOKING_NOT_YET_STARTED(1036, "Booking not yet started", HttpStatus.BAD_REQUEST),
    ROOM_UNAVAILABLE_TIMESLOT(1037, "Room unavailable for the selected time slot", HttpStatus.BAD_REQUEST),
    EQUIPMENT_UNAVAILABLE_TIMESLOT(1038, "Equipment unavailable for the selected time slot", HttpStatus.BAD_REQUEST),
    DEFAULT_EQUIPMENT_CANNOT_BE_BORROWED_SEPARATELY(1039, "Default equipment cannot be borrowed separately", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CANCELLABLE(1040, "Booking not cancellable", HttpStatus.BAD_REQUEST),
    CANCELLATION_WINDOW_EXPIRED(1041, "Cancellation window expired", HttpStatus.BAD_REQUEST),
    BUILDING_EXISTED(1043, "Building existed !", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}