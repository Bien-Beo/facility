package com.utc2.facility.service;

import com.utc2.facility.dto.response.EquipmentModelResponse;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.entity.EquipmentItem;
import com.utc2.facility.entity.EquipmentModel;
import com.utc2.facility.mapper.EquipmentModelMapper;
import com.utc2.facility.repository.EquipmentModelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentModelService {

    EquipmentModelRepository equipmentModelRepository;
    EquipmentModelMapper equipmentModelMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_MANAGER', 'TECHNICIAN')")
    public Page<EquipmentModelResponse> getEquipmentModels(Pageable pageable) {
        Page<EquipmentModel> itemPage = equipmentModelRepository.findAll(pageable);
        return itemPage.map(this::buildFullEquipmentModelResponse); // Map từng item trong page sang response
    }

    private EquipmentModelResponse buildFullEquipmentModelResponse(EquipmentModel model) {
        EquipmentModelResponse response = equipmentModelMapper.toEquipmentModelResponse(model);

        // Lấy thông tin từ các đối tượng liên kết (kiểm tra null)
        if (model.getEquipmentType() != null) {
            response.setEquipmentTypeName(model.getEquipmentType().getName());
        }

        return response;
    }
}