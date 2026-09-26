package com.nagne.carguardplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleEntryDto {

    private String plateNumber;

    // 차량을 구분하기 위한 BLE 식별자
    private String bleId;
}