package com.nagne.carguardplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;

    // 차량별 BLE 식별자
    private String bleId;

    // 최근 감지 위치
    private String location;

    private LocalDateTime timestamp;

    private boolean confirmed = false;
}