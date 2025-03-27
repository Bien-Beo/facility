package com.utc2.facility.repository;

import com.utc2.facility.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(com.utc2.facility.enums.Role name);
}
