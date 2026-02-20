package com.serverwatch.repository;

import com.serverwatch.entity.AlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {
}
