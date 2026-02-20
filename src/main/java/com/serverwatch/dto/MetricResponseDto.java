package com.serverwatch.dto;

import java.time.LocalDateTime;

public class MetricResponseDto {

    private LocalDateTime timestamp;
    private float cpu;
    private float memory;
    private float disk;
    private float loadAvg;

    public MetricResponseDto(LocalDateTime timestamp,
                             float cpu,
                             float memory,
                             float disk,
                             float loadAvg) {
        this.timestamp = timestamp;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
        this.loadAvg = loadAvg;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public float getCpu() { return cpu; }
    public float getMemory() { return memory; }
    public float getDisk() { return disk; }
    public float getLoadAvg() { return loadAvg; }
}
