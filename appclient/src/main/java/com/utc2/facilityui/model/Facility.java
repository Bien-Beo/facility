package com.utc2.facilityui.model;
//
import javafx.beans.property.*; // Import các lớp Property cần thiết

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Import để bắt lỗi parse
import java.util.List;
import java.util.Objects;

/**
 * Model đại diện cho Facility (Room) - Đã sửa lại để dùng kiểu dữ liệu chuẩn
 * cho việc parse Gson và cung cấp các Property getter cho TableView.
 */
public class Facility {

    // --- Sử dụng kiểu dữ liệu chuẩn ---
    private String id;
    private String name;
    private String description;
    private int capacity;
    private String img;
    private String status;
    private String buildingName;
    private String roomTypeName;
    private String nameFacilityManager;
    private String location;
    private String createdAt; // Lưu trữ chuỗi gốc hoặc đã được format
    private String updatedAt; // Lưu trữ chuỗi gốc hoặc đã được format
    private String deletedAt;
    // private List<Object> defaultEquipments; // Tạm thời bỏ qua

    // --- Các đối tượng Property (sẽ được khởi tạo khi cần - Lazy Initialization) ---
    private transient StringProperty idProperty; // transient để Gson bỏ qua khi serialize/deserialize
    private transient StringProperty nameProperty;
    private transient StringProperty descriptionProperty;
    private transient IntegerProperty capacityProperty;
    private transient StringProperty imgProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty buildingNameProperty;
    private transient StringProperty roomTypeNameProperty;
    private transient StringProperty nameFacilityManagerProperty;
    private transient StringProperty locationProperty;
    private transient StringProperty createdAtProperty;
    private transient StringProperty updatedAtProperty;
    private transient StringProperty deletedAtProperty;

    // Định dạng ngày giờ mong muốn hiển thị
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    // Các định dạng ISO có thể có từ API
    private static final DateTimeFormatter ISO_WITH_FRACTIONAL = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Ví dụ: "2025-04-11T16:54:51.447186"
    private static final DateTimeFormatter ISO_WITHOUT_FRACTIONAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // Ví dụ: "2025-04-11T16:54:51"

    // Constructor
    public Facility() {}

    // --- Property Getters (dùng cho TableView - Lazy Initialization) ---
    // Chúng sẽ tạo đối tượng Property khi được gọi lần đầu tiên
    public StringProperty idProperty() {
        if (idProperty == null) idProperty = new SimpleStringProperty(this, "id", id);
        return idProperty;
    }
    public StringProperty nameProperty() {
        if (nameProperty == null) nameProperty = new SimpleStringProperty(this, "name", name);
        return nameProperty;
    }
    public StringProperty descriptionProperty() {
        if (descriptionProperty == null) descriptionProperty = new SimpleStringProperty(this, "description", description);
        return descriptionProperty;
    }
    public IntegerProperty capacityProperty() {
        if (capacityProperty == null) capacityProperty = new SimpleIntegerProperty(this, "capacity", capacity);
        return capacityProperty;
    }
    public StringProperty imgProperty() {
        if (imgProperty == null) imgProperty = new SimpleStringProperty(this, "img", img);
        return imgProperty;
    }
    public StringProperty statusProperty() {
        if (statusProperty == null) statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }
    public StringProperty buildingNameProperty() {
        if (buildingNameProperty == null) buildingNameProperty = new SimpleStringProperty(this, "buildingName", buildingName);
        return buildingNameProperty;
    }
    public StringProperty roomTypeNameProperty() {
        if (roomTypeNameProperty == null) roomTypeNameProperty = new SimpleStringProperty(this, "roomTypeName", roomTypeName);
        return roomTypeNameProperty;
    }
    public StringProperty nameFacilityManagerProperty() {
        if (nameFacilityManagerProperty == null) nameFacilityManagerProperty = new SimpleStringProperty(this, "nameFacilityManager", nameFacilityManager);
        return nameFacilityManagerProperty;
    }
    public StringProperty locationProperty() {
        if (locationProperty == null) locationProperty = new SimpleStringProperty(this, "location", location);
        return locationProperty;
    }
    public StringProperty createdAtProperty() {
        if (createdAtProperty == null) createdAtProperty = new SimpleStringProperty(this, "createdAt", createdAt); // Hiển thị chuỗi đã format
        return createdAtProperty;
    }
    public StringProperty updatedAtProperty() {
        if (updatedAtProperty == null) updatedAtProperty = new SimpleStringProperty(this, "updatedAt", updatedAt); // Hiển thị chuỗi đã format
        return updatedAtProperty;
    }
    public StringProperty deletedAtProperty() {
        if (deletedAtProperty == null) deletedAtProperty = new SimpleStringProperty(this, "deletedAt", deletedAt);
        return deletedAtProperty;
    }

    // --- Standard Getters/Setters (dùng cho Gson và logic khác) ---
    // Gson sẽ gọi các setter này khi parse JSON
    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        // Không cần cập nhật property ở đây, property getter sẽ tự lấy giá trị mới nhất
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImg() { return img; }
    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getNameFacilityManager() { return nameFacilityManager; }
    public void setNameFacilityManager(String nameFacilityManager) {
        this.nameFacilityManager = nameFacilityManager;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) {
        // Format ngày giờ ngay khi nhận từ Gson và lưu chuỗi đã format
        this.createdAt = formatDisplayDateTime(createdAt);
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) {
        // Format ngày giờ ngay khi nhận từ Gson và lưu chuỗi đã format
        this.updatedAt = formatDisplayDateTime(updatedAt);
    }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
    // public List<Object> getDefaultEquipments() { return defaultEquipments; }
    // public void setDefaultEquipments(List<Object> defaultEquipments) { this.defaultEquipments = defaultEquipments; }

    // Helper format ngày giờ từ ISO String sang định dạng hiển thị
    private String formatDisplayDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank() || isoDateTime.equalsIgnoreCase("null")) {
            return "N/A"; // Hoặc trả về null/rỗng tùy ý
        }
        LocalDateTime dateTime = null;
        try {
            // Thử parse với định dạng có phần thập phân giây trước
            dateTime = LocalDateTime.parse(isoDateTime, ISO_WITH_FRACTIONAL);
        } catch (DateTimeParseException e1) {
            try {
                // Nếu lỗi, thử parse với định dạng không có phần thập phân giây
                dateTime = LocalDateTime.parse(isoDateTime, ISO_WITHOUT_FRACTIONAL);
            } catch (DateTimeParseException e2) {
                System.err.println("Không thể format ngày giờ (Facility Model): " + isoDateTime + " - " + e2.getMessage());
                return isoDateTime; // Trả về chuỗi gốc nếu không parse được bằng cả 2 cách
            }
        }
        // Nếu parse thành công bằng 1 trong 2 cách
        return dateTime.format(DISPLAY_FORMATTER);
    }
}