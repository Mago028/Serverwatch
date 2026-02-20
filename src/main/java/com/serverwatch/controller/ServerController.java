package com.serverwatch.controller;

import com.serverwatch.dto.ServerCreateRequest;
import com.serverwatch.dto.ServerResponse;
import com.serverwatch.service.ServerService;
import com.serverwatch.service.MetricQueryService;
import com.serverwatch.service.ServerStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final MetricQueryService metricQueryService;
    private final ServerStatusService serverStatusService;

    // 서버 등록 API (POST /api/server)
    @PostMapping
    public ResponseEntity<ServerResponse> createServer(
            @Valid @RequestBody ServerCreateRequest request
    ) {
        ServerResponse response = serverService.createServer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 서버 목록 조회 (GET /api/servers)
    @GetMapping
    public List<ServerResponse> getServers() {
        return serverService.getServers();
    }

    // 서버 상세 조회 (GET /api/servers/{id})
    @GetMapping("/{id}")
    public ServerResponse getServer(@PathVariable Long id) {
        return serverService.getServer(id);
    }

    // 메트릭 조회 API (GET /api/servers/metrics?range=1h)
    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics(
            @RequestParam(defaultValue = "1h") String range
    ) {
        return ResponseEntity.ok(metricQueryService.getMetricsByRange(range));
    }

    // 상태 조회 (GET /api/servers/{id}/status)
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getServerStatus(@PathVariable Long id) {
        return ResponseEntity.ok(serverStatusService.getServerStatus(id));
    }
}
