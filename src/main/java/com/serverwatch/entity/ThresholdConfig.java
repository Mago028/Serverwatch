package com.serverwatch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "threshold_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ThresholdConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // null 이면 Global 임계치, 값이 있으면 특정 서버용 임계치
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(name = "cpu_limit")
    private Double cpuLimit;

    @Column(name = "mem_limit")
    private Double memLimit;

    @Column(name = "disk_limit")
    private Double diskLimit;

    @Column(name = "load_limit")
    private Double loadLimit;

    // 1 = global, 0 = server별(지금은 global만 사용)
    @Column(name = "global_flag")
    private Boolean globalFlag;
}
