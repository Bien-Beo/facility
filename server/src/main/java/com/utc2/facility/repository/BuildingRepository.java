package com.utc2.facility.repository;

import com.utc2.facility.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {//
    Optional<Building> findByName(String name);
}
