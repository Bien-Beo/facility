package com.utc2.facilityui.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
// import com.google.gson.annotations.SerializedName; // Dùng nếu tên trường JSON khác tên trường Java

public class Facility {

    // --- Trường dữ liệu chuẩn ---
    // Các trường này sẽ được Gson điền trực tiếp với dữ liệu thô từ JSON
    private String id;
    private String name;
    private String description;
    private int capacity;
    private String img;
    private String status;
    private String buildingName;
    private String roomTypeName;
    private String buildingId;
    private String roomTypeId;

    // Trường này sẽ được Gson điền trực tiếp từ JSON key "nameFacilityManager"
    private String nameFacilityManager;
    // Trường này sẽ được Gson điền trực tiếp từ JSON key "facilityManagerId" (nếu có)
    private String facilityManagerId;

    private String location;
    // Các trường này sẽ lưu trữ chuỗi ISO gốc từ server
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    // --- Các đối tượng Property (transient để Gson bỏ qua khi serialize) ---
    // Các property này sẽ được khởi tạo với giá trị đã được format khi cần
    private transient StringProperty idProperty;
    private transient StringProperty nameProperty;
    private transient StringProperty descriptionProperty;
    private transient IntegerProperty capacityProperty;
    private transient StringProperty imgProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty buildingNameProperty;
    private transient StringProperty roomTypeNameProperty;
    private transient StringProperty nameFacilityManagerProperty;
    private transient StringProperty facilityManagerIdProperty;
    private transient StringProperty locationProperty;
    private transient StringProperty formattedCreatedAtProperty; // Property cho chuỗi đã format
    private transient StringProperty formattedUpdatedAtProperty; // Property cho chuỗi đã format
    private transient StringProperty formattedDeletedAtProperty; // Property cho chuỗi đã format

    // --- Định dạng ngày giờ ---
    private static final Locale VIETNAMESE_LOCALE = new Locale("vi", "VN");
    private static final DateTimeFormatter VNF_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy", VIETNAMESE_LOCALE);
    private static final DateTimeFormatter ISO_PARSER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter ISO_WITHOUT_FRACTIONAL_FALLBACK = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Facility() {}

    // --- Helper Method để Format ---
    private String formatDisplayDateTime(String isoDateTimeString) {
        // System.out.println("DEBUG FACILITY MODEL: formatDisplayDateTime CALLED with INPUT: '" + isoDateTimeString + "'");
        if (isoDateTimeString == null || isoDateTimeString.isBlank() || isoDateTimeString.equalsIgnoreCase("null")) {
            return "N/A";
        }
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(isoDateTimeString, ISO_PARSER);
        } catch (DateTimeParseException e1) {
            try {
                dateTime = LocalDateTime.parse(isoDateTimeString, ISO_WITHOUT_FRACTIONAL_FALLBACK);
            } catch (DateTimeParseException e2) {
                System.err.println("Không thể format ngày giờ (Facility Model): '" + isoDateTimeString + "'. Lỗi: " + e2.getMessage());
                return isoDateTimeString; // Trả về gốc nếu không parse được
            }
        }
        return dateTime.format(VNF_DATE_TIME_FORMATTER);
    }

    // --- Property Getters (cho TableView) ---
    // PropertyValueFactory sẽ gọi các phương thức này (hoặc các getter tương ứng nếu không có property method)
    // Tên property ("createdAt", "updatedAt", "nameFacilityManager") phải khớp với chuỗi trong PropertyValueFactory

    public StringProperty idProperty() {
        if (idProperty == null) idProperty = new SimpleStringProperty(this, "id", this.id);
        return idProperty;
    }
    public StringProperty nameProperty() {
        if (nameProperty == null) nameProperty = new SimpleStringProperty(this, "name", this.name);
        return nameProperty;
    }
    public StringProperty descriptionProperty() {
        if (descriptionProperty == null) descriptionProperty = new SimpleStringProperty(this, "description", this.description);
        return descriptionProperty;
    }
    public IntegerProperty capacityProperty() {
        if (capacityProperty == null) capacityProperty = new SimpleIntegerProperty(this, "capacity", this.capacity);
        return capacityProperty;
    }
    public StringProperty imgProperty() {
        if (imgProperty == null) imgProperty = new SimpleStringProperty(this, "img", this.img);
        return imgProperty;
    }
    public StringProperty statusProperty() {
        if (statusProperty == null) statusProperty = new SimpleStringProperty(this, "status", this.status);
        return statusProperty;
    }
    public StringProperty buildingNameProperty() {
        if (buildingNameProperty == null) buildingNameProperty = new SimpleStringProperty(this, "buildingName", this.buildingName);
        return buildingNameProperty;
    }
    public StringProperty roomTypeNameProperty() {
        if (roomTypeNameProperty == null) roomTypeNameProperty = new SimpleStringProperty(this, "roomTypeName", this.roomTypeName);
        return roomTypeNameProperty;
    }
    public StringProperty nameFacilityManagerProperty() {
        if (nameFacilityManagerProperty == null) nameFacilityManagerProperty = new SimpleStringProperty(this, "nameFacilityManager", this.nameFacilityManager);
        return nameFacilityManagerProperty;
    }
    public StringProperty facilityManagerIdProperty() {
        if (facilityManagerIdProperty == null) facilityManagerIdProperty = new SimpleStringProperty(this, "facilityManagerId", this.facilityManagerId);
        return facilityManagerIdProperty;
    }
    public StringProperty locationProperty() {
        if (locationProperty == null) locationProperty = new SimpleStringProperty(this, "location", this.location);
        return locationProperty;
    }

    // Property methods cho các trường ngày giờ đã format
    // Tên property ("createdAt") phải khớp với PropertyValueFactory
    public StringProperty createdAtProperty() {
        if (formattedCreatedAtProperty == null) {
            // Khởi tạo property với giá trị đã được format thông qua getter
            formattedCreatedAtProperty = new SimpleStringProperty(this, "createdAt", getCreatedAt());
        }
        return formattedCreatedAtProperty;
    }
    public StringProperty updatedAtProperty() {
        if (formattedUpdatedAtProperty == null) {
            formattedUpdatedAtProperty = new SimpleStringProperty(this, "updatedAt", getUpdatedAt());
        }
        return formattedUpdatedAtProperty;
    }
    public StringProperty deletedAtProperty() {
        if (formattedDeletedAtProperty == null) {
            formattedDeletedAtProperty = new SimpleStringProperty(this, "deletedAt", getDeletedAt());
        }
        return formattedDeletedAtProperty;
    }

    // --- Standard Getters/Setters ---
    // Setters chỉ gán giá trị thô. Nếu property đã được tạo, chúng sẽ được cập nhật từ getter.
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }

    public String getNameFacilityManager() { return nameFacilityManager; }
    public void setNameFacilityManager(String nameFacilityManager) {
        // System.out.println("DEBUG FACILITY MODEL: setNameFacilityManager (raw) CALLED with: '" + nameFacilityManager + "'");
        this.nameFacilityManager = nameFacilityManager;
        // Nếu property đã được tạo, cập nhật nó
        if (nameFacilityManagerProperty != null) nameFacilityManagerProperty.set(this.nameFacilityManager);
    }

    public String getFacilityManagerId() { return facilityManagerId; }
    public void setFacilityManagerId(String facilityManagerId) {
        this.facilityManagerId = facilityManagerId;
        if (facilityManagerIdProperty != null) facilityManagerIdProperty.set(this.facilityManagerId);
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    // Getters cho ngày giờ sẽ thực hiện format khi được gọi
    public String getCreatedAt() {
        // System.out.println("DEBUG FACILITY MODEL: getCreatedAt CALLED. Raw value is: '" + this.createdAt + "'");
        return formatDisplayDateTime(this.createdAt);
    }
    // Setter cho ngày giờ chỉ lưu giá trị thô từ JSON
    public void setCreatedAt(String createdAtRaw) {
        // System.out.println("DEBUG FACILITY MODEL: setCreatedAt (raw) CALLED with: '" + createdAtRaw + "'");
        this.createdAt = createdAtRaw;
        // Nếu property đã được tạo, cập nhật nó với giá trị đã format
        if (formattedCreatedAtProperty != null) formattedCreatedAtProperty.set(getCreatedAt());
    }

    public String getUpdatedAt() {
        return formatDisplayDateTime(this.updatedAt);
    }
    public void setUpdatedAt(String updatedAtRaw) {
        this.updatedAt = updatedAtRaw;
        if (formattedUpdatedAtProperty != null) formattedUpdatedAtProperty.set(getUpdatedAt());
    }

    public String getDeletedAt() {
        return formatDisplayDateTime(this.deletedAt);
    }
    public void setDeletedAt(String deletedAtRaw) {
        this.deletedAt = deletedAtRaw;
        if (formattedDeletedAtProperty != null) formattedDeletedAtProperty.set(getDeletedAt());
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return Objects.equals(id, facility.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", capacity=" + capacity +
                // ", img='" + img + '\'' + // Thường không cần img trong toString
                ", status='" + status + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", facilityManagerId='" + facilityManagerId + '\'' + // Giữ ID nếu cần
                ", nameFacilityManager='" + nameFacilityManager + '\'' + // Tên người quản lý
                ", location='" + location + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", deletedAt='" + (deletedAt != null ? deletedAt : "N/A") + '\'' +
                '}';
    }
}