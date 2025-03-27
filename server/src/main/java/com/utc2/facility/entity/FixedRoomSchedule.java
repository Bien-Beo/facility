package com.utc2.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "fixed_room_schedule",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"room_id", "day_of_week", "start_time", "end_time", "semester", "academic_year"}
        )
)
public class FixedRoomSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;  // Giá trị từ 1-7 (Thứ Hai - Chủ Nhật)

    @Column(name = "semester", length = 20, nullable = false)
    private String semester;

    @Column(name = "academic_year", length = 9, nullable = false)
    private String academicYear;  // Ví dụ: "2024-2025"
}
