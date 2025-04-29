package com.utc2.facility.entity;

import com.utc2.facility.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "maintenance_tickets",
        indexes = {
                @Index(name = "idx_mt_room_id", columnList = "room_id"),
                @Index(name = "idx_mt_item_id", columnList = "item_id"),
                @Index(name = "idx_mt_technician_id", columnList = "technician_user_id"),
                @Index(name = "idx_mt_status", columnList = "status")
        })
public class MaintenanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id") // Nullable
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // Nullable
    EquipmentItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id")
    User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_user_id")
    User technician;

    @Column(name = "report_date", nullable = false, updatable = false) // Đổi tên từ createdAt
    LocalDateTime reportDate;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT") // Mô tả ban đầu
    String description;

    @Column(name = "action_taken", columnDefinition = "TEXT") // Hành động xử lý (Mới)
    String actionTaken;

    @Column(name = "start_date") // Ngày bắt đầu xử lý (Mới)
    LocalDateTime startDate;

    @Column(name = "completion_date") // Ngày hoàn thành (Mới)
    LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    // Sử dụng Enum mới bao quát hơn: REPORTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANNOT_REPAIR, CANCELLED
    MaintenanceStatus status = MaintenanceStatus.REPORTED; // Đổi tên Enum và giá trị mặc định

    @Column(name = "cost", precision = 10, scale = 2) // Chi phí (Mới)
    BigDecimal cost;

    @Column(name = "notes", columnDefinition = "TEXT") // Ghi chú thêm về quá trình xử lý (Mới)
    String notes;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP") // Tự động cập nhật
    LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        reportDate = LocalDateTime.now(); // Gán giá trị khi tạo mới
        // createdAt = LocalDateTime.now(); // Không cần nữa
    }

    // Có thể thêm @PreUpdate để cập nhật updatedAt nếu không dùng cơ chế DB
}