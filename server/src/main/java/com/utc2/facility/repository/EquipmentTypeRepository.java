package com.utc2.facility.repository;

import com.utc2.facility.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, String>  {//
    Optional<EquipmentType> findByName(String name);
}
