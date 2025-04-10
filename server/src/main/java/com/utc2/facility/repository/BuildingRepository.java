package com.utc2.facility.repository;

import com.utc2.facility.entity.Building;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {
    Optional<Building> findByName(String name);
    Optional<Building> findById(String id);
    boolean existsByName(@NotBlank(message = "Tên tòa nhà không được trống") @Size(max = 255, message = "Tên tòa nhà không được vượt quá 255 ký tự") String name);
}
