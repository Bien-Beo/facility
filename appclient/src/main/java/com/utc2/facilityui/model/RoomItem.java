package com.utc2.facilityui.model;

import java.util.Objects;

public class RoomItem {
    private String id;
    private String name;

    // Constructor
    public RoomItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // No-args constructor (có thể cần thiết cho một số thư viện hoặc cách dùng khác)
    public RoomItem() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString() - ComboBox sẽ sử dụng phương thức này để hiển thị
    // nếu bạn không cung cấp một StringConverter tùy chỉnh.
    // Tuy nhiên, việc sử dụng StringConverter vẫn được khuyến nghị để có sự kiểm soát tốt hơn.
    @Override
    public String toString() {
        return name; // Hiển thị tên phòng trong ComboBox
    }

    // Override equals() và hashCode() nếu bạn cần so sánh các đối tượng RoomItem
    // hoặc sử dụng chúng trong các Collection yêu cầu điều này (ví dụ: HashSet).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomItem roomItem = (RoomItem) o;
        return Objects.equals(id, roomItem.id) &&
                Objects.equals(name, roomItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}