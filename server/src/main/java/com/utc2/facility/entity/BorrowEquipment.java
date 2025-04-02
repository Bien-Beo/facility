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
@Table(name = "borrow_equipment")
public class BorrowEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "borrow_request_id", nullable = false)
    BorrowRequest borrowRequest;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    Equipment equipment;

    @Column(name = "quantity", nullable = false)
    int quantity;
}
