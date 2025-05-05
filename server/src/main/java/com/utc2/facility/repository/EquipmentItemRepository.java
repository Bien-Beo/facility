package com.utc2.facility.repository;

import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.enums.EquipmentStatus; // Import Enum
import org.springframework.data.domain.Page;       // Import Page
import org.springframework.data.domain.Pageable;  // Import Pageable
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, String> {

    // findById(String id) được kế thừa từ JpaRepository, không cần định nghĩa lại

    // Sửa lại tên phương thức và kiểu tham số status
    List<EquipmentItem> findByDefaultRoom_IdAndStatusNot(String defaultRoomId, EquipmentStatus status);

    // Cân nhắc thêm phương thức này nếu cần tìm item không thuộc phòng mặc định nào
    List<EquipmentItem> findByDefaultRoomIsNullAndStatusNot(EquipmentStatus status);

    boolean existsBySerialNumber(String serialNumber);
    boolean existsByAssetTag(String assetTag);
    boolean existsByAssetTagAndIdNot(String assetTag, String id);

    // Cân nhắc thêm các phương thức tìm kiếm khác nếu cần
    Optional<EquipmentItem> findBySerialNumber(String serialNumber);
    Optional<EquipmentItem> findByAssetTag(String assetTag);

    // Tìm item theo model (có thể cần phân trang)
    List<EquipmentItem> findByModel_Id(String modelId);
    Page<EquipmentItem> findByModel_Id(String modelId, Pageable pageable);

    List<EquipmentItem> findByDefaultRoom_Id(String id);

    // Ghi đè findAll để trả về Page (hoặc dùng mặc định của JpaRepository)
    // Page<EquipmentItem> findAll(Pageable pageable);
    boolean existsBySerialNumberAndIdNot(String serialNumber, String id);

    Page<EquipmentItem> findAll(Specification<EquipmentItem> and, Pageable pageable);
}