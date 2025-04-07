package com.utc2.facility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class BookingEquipmentId implements Serializable {

    @Column(name = "booking_id", nullable = false, length = 36)
    String bookingId;

    @Column(name = "item_id", nullable = false, length = 36)
    String itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingEquipmentId that = (BookingEquipmentId) o;
        return Objects.equals(bookingId, that.bookingId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, itemId);
    }
}