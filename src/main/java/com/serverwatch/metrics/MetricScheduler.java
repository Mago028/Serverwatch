package com.serverwatch.metrics;

import com.serverwatch.entity.Server;
import com.serverwatch.entity.ServerMetric;
import com.serverwatch.repository.ServerMetricRepository;
import com.serverwatch.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricScheduler {

    private final MetricCollector metricCollector;
    private final ServerRepository serverRepository;
    private final ServerMetricRepository serverMetricRepository;

    // 30초마다 실행 (30,000ms)
    @Scheduled(fixedRate = 30_000)
    public void collectAndSaveMetrics() {
        // 1대 기준: 제일 먼저 등록된 서버 1개 사용
        Optional<Server> optionalServer = serverRepository.findTop1ByOrderByIdAsc();

        if (optionalServer.isEmpty()) {
            log.warn("수집할 서버가 없습니다. servers 테이블에 서버를 먼저 등록하세요.");
            return;
        }

        Server server = optionalServer.get();

        SystemMetrics metrics = metricCollector.collect();

        ServerMetric metric = ServerMetric.builder()
                .server(server)
                .cpu((float) metrics.getCpuUsage())
                .memory((float) metrics.getMemoryUsage())
                .disk((float) metrics.getDiskUsage())
                .loadAvg((float) metrics.getLoadAverage1m())
                .timestamp(LocalDateTime.now())
                .build();

        serverMetricRepository.save(metric);

        log.info("✅ Metric saved for server(id={}): cpu={}%, mem={}%, disk={}%, load1m={}",
                server.getId(),
                metrics.getCpuUsage(),
                metrics.getMemoryUsage(),
                metrics.getDiskUsage(),
                metrics.getLoadAverage1m());
    }
}
