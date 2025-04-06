package com.utc2.facility.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "booking_equipment")
public class BookingEquipment {

    @EmbeddedId // Sử dụng @EmbeddedId để nhúng lớp khóa chính
    private BookingEquipmentId id; // Tên trường chứa khóa chính nhúng

    @ManyToOne(fetch = FetchType.LAZY) // Bỏ optional = false vì đã được quản lý bởi ID
    @MapsId("bookingId") // Ánh xạ thuộc tính bookingId trong BookingEquipmentId tới khóa chính của Booking
    @JoinColumn(name = "booking_id", nullable = false, insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId") // Ánh xạ thuộc tính itemId trong BookingEquipmentId tới khóa chính của EquipmentItem
    @JoinColumn(name = "item_id", nullable = false, insertable = false, updatable = false)
    private EquipmentItem item; // Đảm bảo tên lớp là EquipmentItem

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_default_equipment", nullable = false)
    private Boolean isDefaultEquipment = false;

    @Lob
    @Column(name = "note")
    private String notes;

    public BookingEquipment(Booking booking, EquipmentItem item, Boolean isDefaultEquipment, String notes) {
        this.id = new BookingEquipmentId(booking.getId(), item.getId());
        this.booking = booking;
        this.item = item;
        this.isDefaultEquipment = isDefaultEquipment;
        this.notes = notes;
    }
}