package com.serverwatch.repository;

import com.serverwatch.entity.ThresholdConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThresholdConfigRepository extends JpaRepository<ThresholdConfig, Long> {

    Optional<ThresholdConfig> findTopByGlobalFlagTrueOrderByIdAsc();
}
