package com.utc2.facility.specification;

import com.utc2.facility.entity.Booking;
import com.utc2.facility.entity.Room;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {
    public static Specification<Room> filterByBuildingId(String buildingId) {
        return (Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (buildingId == null || buildingId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("building").get("id"), buildingId);
        };
    }

    public static Specification<Room> filterByRoomTypeId(String roomTypeId) {
        return (Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (roomTypeId == null || roomTypeId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("roomType").get("id"), roomTypeId);
        };
    }

    public static Specification<Room> filterByFacilityManagerId(String facilityManagerId) {
        return (Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (facilityManagerId == null || facilityManagerId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("facilityManager").get("id"), facilityManagerId);
        };
    }

    public static Specification<Room> filterByYear(Integer year) {
        return (Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdAt")), year));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
