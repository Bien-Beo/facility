package com.utc2.facility.repository;

import com.utc2.facility.entity.EquipmentModel;
import org.springframework.data.domain.Page;      // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentModelRepository extends JpaRepository<EquipmentModel, String> {

    // Bỏ annotation validation không cần thiết ở đây
    Optional<EquipmentModel> findByName(String name);

    // findByModelId là thừa, dùng findById(String id) từ JpaRepository

    // Cân nhắc thêm phương thức tìm model theo type (có phân trang)
    List<EquipmentModel> findByEquipmentType_Id(String typeId);
    Page<EquipmentModel> findByEquipmentType_Id(String typeId, Pageable pageable);

    // Ghi đè findAll để trả về Page (hoặc dùng mặc định của JpaRepository)
    // Page<EquipmentModel> findAll(Pageable pageable);
}