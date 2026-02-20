package com.serverwatch.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class MetricCollector {

    public SystemMetrics collect() {
        if (!isLinux()) {
            log.warn("MetricCollector: 현재 OS({})에서는 /proc 기반 수집이 지원되지 않습니다.",
                    System.getProperty("os.name"));

            return SystemMetrics.builder()
                    .cpuUsage(0.0)
                    .memoryUsage(0.0)
                    .diskUsage(0.0)
                    .loadAverage1m(0.0)
                    .build();
        }

        try {
            double cpu = collectCpuUsage();
            double mem = collectMemoryUsage();
            double disk = collectDiskUsage();
            double load = collectLoadAverage1m();

            return SystemMetrics.builder()
                    .cpuUsage(cpu)
                    .memoryUsage(mem)
                    .diskUsage(disk)
                    .loadAverage1m(load)
                    .build();

        } catch (Exception e) {
            log.error("시스템 메트릭 수집 실패", e);
            return SystemMetrics.builder()
                    .cpuUsage(0.0)
                    .memoryUsage(0.0)
                    .diskUsage(0.0)
                    .loadAverage1m(0.0)
                    .build();
        }
    }


    /* ----------------------- OS 체크 ----------------------- */
    private boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }


    /* ----------------------- CPU 수집 ----------------------- */
    private static class CpuStat {
        long idle;
        long total;
        CpuStat(long idle, long total) {
            this.idle = idle;
            this.total = total;
        }
    }

    private double collectCpuUsage() throws IOException, InterruptedException {
        CpuStat stat1 = readCpuStat();
        Thread.sleep(1000);
        CpuStat stat2 = readCpuStat();

        long idleDiff = stat2.idle - stat1.idle;
        long totalDiff = stat2.total - stat1.total;
        if (totalDiff == 0) return 0.0;

        double usage = (double) (totalDiff - idleDiff) / totalDiff * 100.0;
        return round1(usage);
    }

    private CpuStat readCpuStat() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/proc/stat"));
        String cpuLine = lines.stream()
                .filter(line -> line.startsWith("cpu "))
                .findFirst()
                .orElseThrow();

        String[] p = cpuLine.trim().split("\\s+");
        long user = Long.parseLong(p[1]);
        long nice = Long.parseLong(p[2]);
        long system = Long.parseLong(p[3]);
        long idle = Long.parseLong(p[4]);
        long iowait = Long.parseLong(p[5]);
        long irq = Long.parseLong(p[6]);
        long softirq = Long.parseLong(p[7]);
        long steal = p.length > 8 ? Long.parseLong(p[8]) : 0;

        long idleAll = idle + iowait;
        long nonIdle = user + nice + system + irq + softirq + steal;
        long total = idleAll + nonIdle;

        return new CpuStat(idleAll, total);
    }


    /* ----------------------- 메모리 수집 ----------------------- */
    private double collectMemoryUsage() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/proc/meminfo"));

        long total = 0;
        long available = 0;

        for (String line : lines) {
            if (line.startsWith("MemTotal:")) total = parseKb(line);
            if (line.startsWith("MemAvailable:")) available = parseKb(line);
        }

        if (total == 0) return 0.0;

        double usedRatio = 1 - (double) available / total;
        return round1(usedRatio * 100.0);
    }

    private long parseKb(String line) {
        return Long.parseLong(line.split("\\s+")[1]);
    }


    /* ----------------------- 디스크 수집 ----------------------- */
    private double collectDiskUsage() {
        File root = new File("/");
        long total = root.getTotalSpace();
        long free = root.getFreeSpace();
        if (total == 0) return 0.0;

        double used = (double) (total - free) / total * 100.0;
        return round1(used);
    }


    /* ----------------------- Load Average 1m ----------------------- */
    private double collectLoadAverage1m() throws IOException {
        String content = Files.readString(Paths.get("/proc/loadavg"));
        String[] parts = content.split("\\s+");
        return round2(Double.parseDouble(parts[0]));
    }


    /* ----------------------- 유틸 ----------------------- */
    private double round1(double v) { return Math.round(v * 10) / 10.0; }
    private double round2(double v) { return Math.round(v * 100) / 100.0; }
}
