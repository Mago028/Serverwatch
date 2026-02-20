package com.serverwatch.repository;

import com.serverwatch.entity.ServerMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerMetricRepository extends JpaRepository<ServerMetric, Long> {
    List<ServerMetric> findByTimestampBetweenOrderByTimestampAsc(LocalDateTime start,
                                                                 LocalDateTime end);
    ServerMetric findTopByServerIdOrderByTimestampDesc(Long serverId);


}
