package com.serverwatch.repository;

import com.serverwatch.entity.Server;
import com.serverwatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {

    // 가장 먼저 등록된 서버 1대를 가져오는 메서드 (스케줄러용)
    Optional<Server> findTop1ByOrderByIdAsc();
    List<Server> findByUser(User user);
}