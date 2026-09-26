package com.nagne.carguardplatform.service;

import com.nagne.carguardplatform.dto.WarningDto;
import com.nagne.carguardplatform.entity.Vehicle;
import com.nagne.carguardplatform.entity.Warning;
import com.nagne.carguardplatform.repository.VehicleRepository;
import com.nagne.carguardplatform.repository.WarningRepository;
import com.nagne.carguardplatform.util.WarningStreamManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarningService {

    private final WarningRepository warningRepository;
    private final VehicleRepository vehicleRepository;

    // 미등록 차량 경고 생성
    public void issueWarning(WarningDto dto) {

        // 등록 차량이면 경고 생성하지 않음
        if (vehicleRepository.findById(dto.getPlateNumber())
                .map(Vehicle::isRegistered)
                .orElse(false)) {

            throw new IllegalArgumentException(
                    "등록된 차량은 경고 대상이 아닙니다."
            );
        }

        Warning warning = new Warning();

        warning.setPlateNumber(dto.getPlateNumber());
        warning.setBleId(dto.getBleId());

        // 입차 직후에는 아직 위치를 모름
        warning.setLocation(null);

        warning.setTimestamp(LocalDateTime.now());

        warningRepository.save(warning);

        // 미등록 차량 입차 알림
        WarningStreamManager.send(
                "warning",
                "차량번호: " + dto.getPlateNumber()
        );
    }

    public void updateTrackingLocation(
            String bleId,
            String location
    ) {

        if (bleId == null || bleId.isBlank()) {
            throw new IllegalArgumentException(
                    "BLE ID가 없습니다."
            );
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException(
                    "위치 정보가 없습니다."
            );
        }

        Warning warning = warningRepository
                .findTopByBleIdOrderByTimestampDesc(bleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 BLE 차량의 미등록 기록이 없습니다."
                        )
                );

        String previousLocation =
                warning.getLocation();

        warning.setLocation(location);

        warningRepository.save(warning);

        // 위치가 처음 확인되었거나 변경된 경우만 알림
        if (
                previousLocation == null
                        || !previousLocation.equals(location)
        ) {

            WarningStreamManager.send(
                    "location",
                    warning.getPlateNumber()
                            + " · "
                            + location
            );
        }
    }

    // 전체 경고 조회
    public List<WarningDto> findAllWarnings() {
        return warningRepository.findAll().stream()
                .map(w -> {
                    WarningDto dto = new WarningDto();

                    dto.setId(w.getId());
                    dto.setPlateNumber(w.getPlateNumber());
                    dto.setLocation(w.getLocation());

                    dto.setTimestamp(
                            w.getTimestamp() != null
                                    ? w.getTimestamp().toString()
                                    : null
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 조건별 경고 조회
    public List<WarningDto> filterWarnings(
            String plateNumber,
            String location,
            LocalDate date
    ) {
        return warningRepository
                .filterWarnings(plateNumber, location, date)
                .stream()
                .map(w -> {
                    WarningDto dto = new WarningDto();

                    dto.setId(w.getId());
                    dto.setPlateNumber(w.getPlateNumber());
                    dto.setLocation(w.getLocation());

                    dto.setTimestamp(
                            w.getTimestamp() != null
                                    ? w.getTimestamp().toString()
                                    : null
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 경고 확인 처리
    public void confirmWarning(Long id) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 경고가 존재하지 않습니다: " + id
                        )
                );

        warning.setConfirmed(true);
        warningRepository.save(warning);
    }
}