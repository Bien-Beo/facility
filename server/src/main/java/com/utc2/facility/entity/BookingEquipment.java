package com.utc2.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "booking_equipment")
public class BorrowEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "borrow_request_id", nullable = false)
    Booking booking;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    EquipmentItem equipmentItem;
}//
