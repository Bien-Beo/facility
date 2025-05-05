package com.utc2.facility.specification;

import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.Room;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSpecification {
    public static Specification<EquipmentItem> filterByRoomId(String roomId) {
        return (Root<EquipmentItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (roomId == null || roomId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("defaultRoom").get("id"), roomId);
        };
    }

    public static Specification<EquipmentItem> filterByModelId(String modelId) {
        return (Root<EquipmentItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (modelId == null || modelId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("model").get("id"), modelId);
        };
    }

    public static Specification<EquipmentItem> filterByYear(Integer year) {
        return (Root<EquipmentItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("purchaseDate")), year));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
