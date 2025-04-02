package com.utc2.facility.service;

import com.utc2.facility.dto.request.EquipmentCreationRequest;
import com.utc2.facility.dto.request.RoomCreationRequest;
import com.utc2.facility.dto.response.EquipmentResponse;
import com.utc2.facility.dto.response.RoomResponse;
import com.utc2.facility.entity.*;
import com.utc2.facility.exception.AppException;
import com.utc2.facility.exception.ErrorCode;
import com.utc2.facility.mapper.EquipmentMapper;
import com.utc2.facility.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentService {

    EquipmentRepository equipmentRepository;
    UserRepository userRepository;
    EquipmentTypeRepository equipmentTypeRepository;
    RoomRepository roomRepository;
    EquipmentMapper equipmentMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentResponse createEquipment(EquipmentCreationRequest request) {

        User equipmentManager = userRepository.findByUserId(request.getEquipmentManagerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        EquipmentType equipmentType = equipmentTypeRepository.findByName(request.getEquipmentTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_TYPE_NOT_FOUND));
        Room room = roomRepository.findByName(request.getRoomName())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Equipment equipment = equipmentMapper.toEquipment(request);
        equipment.setEquipmentManager(equipmentManager);
        equipment.setEquipmentType(equipmentType);
        equipment.setRoom(room);

        return equipmentMapper.toEquipmentResponse(equipmentRepository.save(equipment));
    }

    public EquipmentResponse getEquipmentByName(String name) {
        return equipmentMapper.toEquipmentResponse(equipmentRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND)));
    }

    public List<EquipmentResponse> getEquipments() {
        return equipmentRepository.findAll().stream().map(equipmentMapper::toEquipmentResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEquipment(String slug) {
        Equipment equipment = equipmentRepository.findByName(slug)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));
        equipmentRepository.delete(equipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentResponse updateEquipment(EquipmentCreationRequest request, String slug) {
        Equipment equipment = equipmentRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_NOT_FOUND));

        if (request.getEquipmentTypeName() != null) {
            EquipmentType equipmentType = equipmentTypeRepository.findByName(request.getEquipmentTypeName())
                    .orElseThrow(() -> new AppException(ErrorCode.EQUIPMENT_TYPE_NOT_FOUND));
            equipment.setEquipmentType(equipmentType);
        }

        if (request.getRoomName() != null) {
            Room room = roomRepository.findByName(request.getRoomName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
            equipment.setRoom(room);
        }

        if (request.getEquipmentManagerId() != null) {
            User equipmentManager = userRepository.findByUserId(request.getEquipmentManagerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            equipment.setEquipmentManager(equipmentManager);
        }

        return equipmentMapper.toEquipmentResponse(equipmentRepository.save(equipment));
    }

}
