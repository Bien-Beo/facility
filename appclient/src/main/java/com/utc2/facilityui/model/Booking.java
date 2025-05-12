package com.utc2.facilityui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Booking {
    private final StringProperty titleFacility;
    private final StringProperty requestedBy;
    private final StringProperty purpose;
    private final StringProperty timeSlot;
    private final StringProperty requestedAt;
    private final StringProperty groupDirector;
    private final StringProperty facilityMan;
    private final StringProperty equipment;
    private final StringProperty canceled;// Thuộc tính mới

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter REQUESTED_AT_FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd yyyy hh:mm a");

    public Booking(String titleFacility, String requestedBy, String purpose, LocalDateTime date,
                   String timeSlot, LocalDateTime requestedAt, String groupDirector, String facilityMan, String equipment, String canceled) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        // Tách và định dạng timeSlot
        String[] times = timeSlot.split("-");
        LocalTime startTime = LocalTime.parse(times[0].trim());
        LocalTime endTime = LocalTime.parse(times[1].trim());
        String formattedTimeSlot = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
        this.titleFacility = new SimpleStringProperty(requestedBy + " / " + titleFacility);
        this.requestedBy = new SimpleStringProperty(requestedBy);
        this.purpose = new SimpleStringProperty(purpose);
        this.timeSlot = new SimpleStringProperty(date.format(dateFormatter) + " " + formattedTimeSlot);
        this.requestedAt = new SimpleStringProperty(requestedAt.format(DATE_TIME_FORMATTER));
        this.groupDirector = new SimpleStringProperty(groupDirector);
        this.facilityMan = new SimpleStringProperty(facilityMan);
        this.equipment = new SimpleStringProperty(equipment);
        this.canceled = new SimpleStringProperty(canceled);// Khởi tạo thuộc tính mới
    }

    // Getter cho equipment
    public String getEquipment() {
        return equipment.get();
    }

    // Các phương thức Property
    public ObservableValue<String> titleFacilityProperty() {
        return titleFacility;
    }

    public ObservableValue<String> purposeProperty() {
        return purpose;
    }

    public ObservableValue<String> timeSlotProperty() {
        return timeSlot;
    }

    public ObservableValue<String> requestedAtProperty() {
        return new SimpleStringProperty(getRequestedAt());
    }

    public ObservableValue<String> equipmentProperty() {
        return equipment;  // Trả về equipmentProperty
    }

    public ObservableValue<String> statusProperty() {
        return new SimpleStringProperty(getStatus());
    }

    public ObservableValue<String> handledByProperty() {
        return new SimpleStringProperty(getHandledBy());
    }

    public ObservableValue<String> reasonNoteProperty() {
        return new SimpleStringProperty(getReasonNote());
    }

    // Các phương thức setter cho các thuộc tính
    public void setTitleFacility(String titleFacility) {
        this.titleFacility.set(titleFacility);
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy.set(requestedBy);
    }

    public void setPurpose(String purpose) {
        this.purpose.set(purpose);
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot.set(timeSlot);
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt.set(requestedAt);
    }

    public void setGroupDirector(String groupDirector) {
        this.groupDirector.set(groupDirector);
    }

    public void setFacilityMan(String facilityMan) {
        this.facilityMan.set(facilityMan);
    }

    public void setEquipment(String equipment) {
        this.equipment.set(equipment);  // Cập nhật giá trị thiết bị
    }

    // Các phương thức getter khác và phương thức xử lý trạng thái vẫn không thay đổi
    public String getTitleFacility() {
        return titleFacility.get();
    }

    public String getRequestedBy() {
        return requestedBy.get();
    }

    public String getPurpose() {
        return purpose.get();
    }

    public String getTimeSlot() {
        return timeSlot.get();
    }

    public StringProperty canceledProperty() {
        return canceled;
    }

    public String getCanceled() {
        return canceled.get();
    }

    public void setCanceled(String canceled) {
        this.canceled.set(canceled);
    }

    public String getRequestedAt() {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(requestedAt.get(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return dateTime.format(REQUESTED_AT_FORMATTER);
        } catch (Exception e) {
            return requestedAt.get(); // fallback nếu parse lỗi
        }
    }

    public String getStatus() {
        if ("true".equals(canceled.get())) {
            return "Đã hủy";
        } else if (groupDirector.get() != null && facilityMan.get() != null) {
            // Kiểm tra nếu đang sử dụng hôm nay
            LocalDate bookingDate = getDate().toLocalDate(); // ngày dự kiến
            LocalDate today = LocalDate.now();

            if (bookingDate.equals(today)) {
                return "Đang sử dụng";
            }
            return "Đã duyệt";
        } else {
            return "Chờ duyệt";
        }
    }



    public String getHandledBy() {
        if (facilityMan.get() != null) {
            return facilityMan.get();
        } else if (groupDirector.get() != null) {
            return groupDirector.get();
        } else {
            return "Chưa xử lý";
        }
    }

    public String getReasonNote() {
        return purpose.get();
    }

    public LocalDateTime getDate() {
        return LocalDateTime.parse(requestedAt.get(), DATE_TIME_FORMATTER);
    }

    public String getGroupDirector() {
        return groupDirector.get();
    }

    public String getFacilityMan() {
        return facilityMan.get();
    }

    public String getTitleFacilityOriginal() {
        return titleFacility.get().split(" / ", 2)[1];
    }
}
