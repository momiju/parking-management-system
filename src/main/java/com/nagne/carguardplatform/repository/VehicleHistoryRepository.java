package com.nagne.carguardplatform.repository;

import com.nagne.carguardplatform.entity.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {
    List<VehicleHistory> findAllByOrderByDetectedAtDesc();
}