package com.serverwatch.service;

import com.serverwatch.dto.MetricResponseDto;
import com.serverwatch.entity.ServerMetric;
import com.serverwatch.repository.ServerMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricQueryService {

    private final ServerMetricRepository serverMetricRepository;

    public List<MetricResponseDto> getMetricsByRange(String range) {
        long hours = parseRangeToHours(range);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(hours);

        List<ServerMetric> list =
                serverMetricRepository.findByTimestampBetweenOrderByTimestampAsc(start, end);

        return list.stream()
                .map(this::toDto)
                .toList();
    }

    private long parseRangeToHours(String range) {
        if (range == null || range.isBlank()) return 1L;

        String r = range.trim().toLowerCase();
        if (r.endsWith("h")) {
            try { return Long.parseLong(r.substring(0, r.length() - 1)); }
            catch (Exception e) { return 1L; }
        }
        return 1L;
    }

    private MetricResponseDto toDto(ServerMetric m) {
        return new MetricResponseDto(
                m.getTimestamp(),
                m.getCpu(),
                m.getMemory(),
                m.getDisk(),
                m.getLoadAvg()
        );
    }
}