package com.nagne.carguardplatform.service;

import com.nagne.carguardplatform.dto.VehicleDto;
import com.nagne.carguardplatform.dto.VehicleEntryDto;
import com.nagne.carguardplatform.dto.VehicleHistoryDto;
import com.nagne.carguardplatform.dto.WarningDto;
import com.nagne.carguardplatform.entity.Vehicle;
import com.nagne.carguardplatform.entity.VehicleHistory;
import com.nagne.carguardplatform.repository.VehicleHistoryRepository;
import com.nagne.carguardplatform.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final WarningService warningService;

    // 등록 차량 저장
    public void save(VehicleDto dto) {

        Vehicle vehicle = new Vehicle(
                dto.getPlateNumber(),
                dto.isRegistered(),
                dto.getLocation(),
                dto.getLastSeen()
        );

        vehicleRepository.save(vehicle);
    }

    // 차량 입차 처리
    public void processEntry(VehicleEntryDto dto) {
        String plateNumber = dto.getPlateNumber();
        String bleId = dto.getBleId();

        if (plateNumber == null || plateNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "차량번호가 없습니다."
            );
        }

        if (bleId == null || bleId.isBlank()) {
            throw new IllegalArgumentException(
                    "BLE ID가 없습니다."
            );
        }

        plateNumber = plateNumber.trim();

        LocalDateTime detectedAt = LocalDateTime.now();

        // 사전에 등록된 차량인지 확인
        Vehicle vehicle = vehicleRepository
                .findById(plateNumber)
                .orElse(null);

        boolean registered =
                vehicle != null && vehicle.isRegistered();

        // 등록/미등록 여부와 관계없이
        // 모든 차량 입차 기록 저장
        VehicleHistory history = new VehicleHistory();

        history.setPlateNumber(plateNumber);
        history.setRegistered(registered);
        history.setDetectedAt(detectedAt);

        vehicleHistoryRepository.save(history);

        // 등록 차량이면 여기서 끝
        if (registered) {
            return;
        }

        // 미등록 차량이면 경고 생성
        WarningDto warningDto = new WarningDto();

        warningDto.setPlateNumber(plateNumber);
        warningDto.setBleId(dto.getBleId());

        warningService.issueWarning(warningDto);
    }

    // 등록 차량만 조회
    public List<VehicleDto> findRegisteredVehicles() {

        return vehicleRepository
                .findByRegisteredTrue()
                .stream()
                .map(v -> new VehicleDto(
                        v.getPlateNumber(),
                        v.isRegistered(),
                        v.getLocation(),
                        v.getLastSeen()
                ))
                .collect(Collectors.toList());
    }

    // 전체 차량 출입 기록 조회
    public List<VehicleHistoryDto> findAllVehicleHistory() {

        return vehicleHistoryRepository
                .findAllByOrderByDetectedAtDesc()
                .stream()
                .map(h -> new VehicleHistoryDto(
                        h.getId(),
                        h.getPlateNumber(),
                        h.isRegistered(),
                        h.getDetectedAt()
                ))
                .collect(Collectors.toList());
    }

    // 등록 차량 삭제
    public void delete(String plateNumber) {

        if (!vehicleRepository.existsById(plateNumber)) {
            throw new IllegalArgumentException(
                    "해당 차량이 존재하지 않습니다: "
                            + plateNumber
            );
        }

        vehicleRepository.deleteById(plateNumber);
    }
}