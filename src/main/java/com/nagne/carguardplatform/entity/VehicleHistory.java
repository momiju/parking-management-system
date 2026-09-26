package com.nagne.carguardplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VehicleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 출입 차량번호
    private String plateNumber;

    // 출입 당시 등록 차량 여부
    private boolean registered;

    // 입차 감지 시간
    private LocalDateTime detectedAt;
}