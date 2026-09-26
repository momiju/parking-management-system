package com.nagne.carguardplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VehicleHistoryDto {

    private Long id;
    private String plateNumber;
    private boolean registered;
    private LocalDateTime detectedAt;
}