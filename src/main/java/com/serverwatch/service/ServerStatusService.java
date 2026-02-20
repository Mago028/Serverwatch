package com.serverwatch.service;

import com.serverwatch.dto.ServerStatus;
import com.serverwatch.entity.AlarmLog;
import com.serverwatch.entity.Server;
import com.serverwatch.entity.ServerMetric;
import com.serverwatch.entity.ThresholdConfig;
import com.serverwatch.notifier.Notifier;
import com.serverwatch.repository.AlarmLogRepository;
import com.serverwatch.repository.ServerMetricRepository;
import com.serverwatch.repository.ServerRepository;
import com.serverwatch.repository.ThresholdConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerStatusService {

    private final ServerMetricRepository metricRepository;
    private final ThresholdConfigRepository thresholdConfigRepository;
    private final AlarmLogRepository alarmLogRepository;
    private final ServerRepository serverRepository;
    private final List<Notifier> notifiers;

    // Global 임계치 1개만 사용 (global_flag = true)
    private ThresholdConfig getGlobalThreshold() {
        return thresholdConfigRepository.findTopByGlobalFlagTrueOrderByIdAsc()
                .orElseGet(this::defaultThreshold);
    }

    // DB에 아무 것도 없을 때 사용할 기본 임계치
    private ThresholdConfig defaultThreshold() {
        return ThresholdConfig.builder()
                .cpuLimit(80.0)   // CPU 80% 이상 HIGH
                .memLimit(80.0)   // MEM 80% 이상 HIGH
                .diskLimit(90.0)  // DISK 90% 이상 HIGH
                .loadLimit(4.0)   // LOAD 4.0 이상 HIGH
                .globalFlag(true)
                .build();
    }

    public ServerStatus getServerStatus(Long serverId) {

        ServerMetric metric = metricRepository
                .findTopByServerIdOrderByTimestampDesc(serverId);

        if (metric == null) {
            return ServerStatus.LOW; // 메트릭 없으면 일단 LOW
        }

        ThresholdConfig th = getGlobalThreshold();

        double cpu = metric.getCpu() != null ? metric.getCpu() : 0.0;
        double memory = metric.getMemory() != null ? metric.getMemory() : 0.0;
        double disk = metric.getDisk() != null ? metric.getDisk() : 0.0;
        double load = metric.getLoadAvg() != null ? metric.getLoadAvg() : 0.0;

        // limit 1개 기준: limit 이상 HIGH, 0.7 * limit 이상 MEDIUM
        ServerStatus cpuStatus  = compare(cpu,   th.getCpuLimit());
        ServerStatus memStatus  = compare(memory, th.getMemLimit());
        ServerStatus diskStatus = compare(disk,  th.getDiskLimit());
        ServerStatus loadStatus = compare(load,  th.getLoadLimit());

        ServerStatus finalStatus = maxOf(cpuStatus, memStatus, diskStatus, loadStatus);

        // HIGH 또는 MEDIUM일 때만 알람
        if (finalStatus != ServerStatus.LOW) {
            createAlarmLog(serverId, finalStatus, cpu, memory, disk, load);
        }

        return finalStatus;
    }

    // 임계치 비교 로직 (value, limit)
    private ServerStatus compare(double value, Double limit) {
        if (limit == null) {
            return ServerStatus.LOW;
        }
        double mediumThreshold = limit * 0.7; // limit의 70% 이상부터 MEDIUM
        if (value >= limit) return ServerStatus.HIGH;
        if (value >= mediumThreshold) return ServerStatus.MEDIUM;
        return ServerStatus.LOW;
    }

    private ServerStatus maxOf(ServerStatus... statuses) {
        ServerStatus max = ServerStatus.LOW;
        for (ServerStatus s : statuses) {
            if (s.ordinal() > max.ordinal()) {
                max = s;
            }
        }
        return max;
    }

    // AlarmLog DB 저장 + Notifier 호출
    private void createAlarmLog(Long serverId,
                                ServerStatus status,
                                double cpu, double memory, double disk, double load) {

        Optional<Server> optionalServer = serverRepository.findById(serverId);
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        String message = String.format(
                "Server[%s] 상태: %s (CPU=%.1f, MEM=%.1f, DISK=%.1f, LOAD=%.2f)",
                server.getName(), status, cpu, memory, disk, load
        );

        AlarmLog alarmLog = AlarmLog.builder()
                .server(server)
                .status(status)
                .message(message)
                .build();

        alarmLogRepository.save(alarmLog);

        for (Notifier notifier : notifiers) {
            notifier.notify(alarmLog);
        }
    }
}
