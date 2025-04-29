package com.utc2.facilityui.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.google.gson.annotations.SerializedName; // Import cho @SerializedName
import java.util.Objects; // Import cho equals và hashCode

/**
 * Model đại diện cho Facility (Room) - Đã cập nhật để sử dụng facilityManagerId.
 */
public class Facility {

    // --- Sử dụng kiểu dữ liệu chuẩn ---
    private String id;
    private String name;
    private String description;
    private int capacity; // Giữ nguyên int như model gốc bạn cung cấp
    private String img;
    private String status;
    private String buildingName;
    private String roomTypeName;

    // Quan trọng: Nếu JSON từ API vẫn gửi "nameFacilityManager", hãy dùng @SerializedName
    // @SerializedName("nameFacilityManager")
    private String facilityManagerId;


    private String location;
    private String createdAt; // Lưu trữ chuỗi gốc hoặc đã được format
    private String updatedAt; // Lưu trữ chuỗi gốc hoặc đã được format
    private String deletedAt;
    // private List<Object> defaultEquipments; // Vẫn tạm thời bỏ qua

    // --- Các đối tượng Property (transient để Gson bỏ qua) ---
    private transient StringProperty idProperty;
    private transient StringProperty nameProperty;
    private transient StringProperty descriptionProperty;
    private transient IntegerProperty capacityProperty; // Property vẫn là IntegerProperty
    private transient StringProperty imgProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty buildingNameProperty;
    private transient StringProperty roomTypeNameProperty;


    private transient StringProperty facilityManagerIdProperty;

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
        // Dù trường là int, property thường là IntegerProperty để xử lý null tốt hơn trong UI bindings
        // (mặc dù trường int này không thể null, nhưng đây là cách làm phổ biến)
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

    public StringProperty facilityManagerIdProperty() { // Đã đổi tên phương thức
        // Đổi tên biến và tên trong constructor SimpleStringProperty
        if (facilityManagerIdProperty == null) {
            facilityManagerIdProperty = new SimpleStringProperty(this, "facilityManagerId", facilityManagerId);
        }
        return facilityManagerIdProperty;
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
        if (deletedAtProperty == null) deletedAtProperty = new SimpleStringProperty(this, "deletedAt", deletedAt); // Hiển thị chuỗi đã format (nếu có)
        return deletedAtProperty;
    }

    // --- Standard Getters/Setters (dùng cho Gson và logic khác) ---
    // Cập nhật: Các setter giờ cũng sẽ cập nhật giá trị property nếu property đã được khởi tạo
    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        // Cập nhật property nếu nó đã được khởi tạo (cho binding UI)
        if (idProperty != null) idProperty.set(id);
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        if (nameProperty != null) nameProperty.set(name);
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        if (descriptionProperty != null) descriptionProperty.set(description);
    }

    public int getCapacity() { return capacity; } // Vẫn trả về int
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (capacityProperty != null) capacityProperty.set(capacity);
    }

    public String getImg() { return img; }
    public void setImg(String img) {
        this.img = img;
        if (imgProperty != null) imgProperty.set(img);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        if (statusProperty != null) statusProperty.set(status);
    }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
        if (buildingNameProperty != null) buildingNameProperty.set(buildingName);
    }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
        if (roomTypeNameProperty != null) roomTypeNameProperty.set(roomTypeName);
    }

    public String getFacilityManagerId() { // Đã đổi tên getter
        return facilityManagerId;
    }
    public void setFacilityManagerId(String facilityManagerId) { // Đã đổi tên setter và tham số
        this.facilityManagerId = facilityManagerId;
        if (facilityManagerIdProperty != null) facilityManagerIdProperty.set(facilityManagerId); // Cập nhật property
    }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
        if (locationProperty != null) locationProperty.set(location);
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) {
        // Format ngày giờ ngay khi nhận từ Gson và lưu chuỗi đã format
        this.createdAt = formatDisplayDateTime(createdAt);
        if (createdAtProperty != null) createdAtProperty.set(this.createdAt);
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) {
        // Format ngày giờ ngay khi nhận từ Gson và lưu chuỗi đã format
        this.updatedAt = formatDisplayDateTime(updatedAt);
        if (updatedAtProperty != null) updatedAtProperty.set(this.updatedAt);
    }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) {
        // Format ngày giờ ngay khi nhận từ Gson và lưu chuỗi đã format (hoặc xử lý null)
        this.deletedAt = formatDisplayDateTime(deletedAt); // Áp dụng format tương tự nếu cần
        if (deletedAtProperty != null) deletedAtProperty.set(this.deletedAt);
    }
    // public List<Object> getDefaultEquipments() { return defaultEquipments; }
    // public void setDefaultEquipments(List<Object> defaultEquipments) { this.defaultEquipments = defaultEquipments; }

    // Helper format ngày giờ từ ISO String sang định dạng hiển thị
    private String formatDisplayDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank() || isoDateTime.equalsIgnoreCase("null")) {
            return "N/A"; // Hoặc trả về null/rỗng tùy ý nếu muốn phân biệt trạng thái "chưa có"
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

    // Override equals() và hashCode() để dùng trong Collection hoặc so sánh đối tượng
    // Thường dựa trên ID nếu nó là duy nhất và không thay đổi
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return Objects.equals(id, facility.id); // So sánh dựa trên ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash dựa trên ID
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", facilityManagerId='" + facilityManagerId + '\'' + // Đã đổi tên
                ", location='" + location + '\'' +
                // Thêm các trường khác nếu cần
                '}';
    }
}