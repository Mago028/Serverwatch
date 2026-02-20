package com.serverwatch.metrics;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemMetrics {

    private final double cpuUsage;        // CPU 사용률 (%)
    private final double memoryUsage;     // 메모리 사용률 (%)
    private final double diskUsage;       // 디스크 사용률 (%)
    private final double loadAverage1m;   // 1분 load average
}