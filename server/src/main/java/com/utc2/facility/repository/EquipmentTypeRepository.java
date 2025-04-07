package com.utc2.facility.repository;

import com.utc2.facility.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, String>  {
    Optional<EquipmentType> findByName(String name);
}
