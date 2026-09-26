package com.nagne.carguardplatform.controller;

import com.nagne.carguardplatform.dto.VehicleDto;
import com.nagne.carguardplatform.dto.VehicleEntryDto;
import com.nagne.carguardplatform.dto.VehicleHistoryDto;
import com.nagne.carguardplatform.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // 차량 사전 등록
    @PostMapping
    public ResponseEntity<String> save(
            @RequestBody VehicleDto dto
    ) {

        vehicleService.save(dto);

        return ResponseEntity.ok(
                "차량 저장 완료"
        );
    }

    // 등록 차량 삭제
    @DeleteMapping("/{plateNumber}")
    public ResponseEntity<String> delete(
            @PathVariable String plateNumber
    ) {

        vehicleService.delete(plateNumber);

        return ResponseEntity.ok(
                "차량 삭제 완료"
        );
    }

    // 등록 차량 목록
    @GetMapping("/registered")
    public ResponseEntity<List<VehicleDto>>
    findRegisteredVehicles() {

        return ResponseEntity.ok(
                vehicleService.findRegisteredVehicles()
        );
    }

    // 전체 차량 출입 기록
    @GetMapping("/history")
    public ResponseEntity<List<VehicleHistoryDto>>
    findAllVehicleHistory() {

        return ResponseEntity.ok(
                vehicleService.findAllVehicleHistory()
        );
    }

    // 차량 입차
    @PostMapping("/entry")
    public ResponseEntity<String> entry(
            @RequestBody VehicleEntryDto dto
    ) {

        vehicleService.processEntry(dto);

        return ResponseEntity.ok(
                "차량 입차 처리 완료"
        );
    }
}