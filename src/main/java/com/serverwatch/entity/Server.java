package com.serverwatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "servers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                       // 서버 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")         // 소유 사용자 ID (FK)
    private User user;                    // 서버 소유자

    @Column(nullable = false, length = 100)
    private String name;                   // 서버 이름

    @Column(length = 255)
    private String description;            // 설명

    @Column(name = "created_at")
    private LocalDateTime createdAt;       // 등록일

    @Column(length = 20)
    private String status;                 // GREEN / YELLOW / RED

    @Column(name = "last_check_at")
    private LocalDateTime lastCheckAt;     // 마지막 모니터링 시간

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(String status, LocalDateTime checkTime) {
        this.status = status;
        this.lastCheckAt = checkTime;
    }
}
