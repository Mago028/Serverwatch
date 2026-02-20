package com.serverwatch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ServerResponse {

    private Long id;
    private String name;
    private String description;
    private String status;          // GREEN / YELLOW / RED / UP 등
    private LocalDateTime createdAt;
    private LocalDateTime lastCheckAt;
}
