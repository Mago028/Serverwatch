package com.serverwatch.service;

import com.serverwatch.dto.ServerCreateRequest;
import com.serverwatch.dto.ServerResponse;
import com.serverwatch.entity.Server;
import com.serverwatch.entity.User;
import com.serverwatch.repository.ServerRepository;
import com.serverwatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;

    // 🔹 현재 로그인한 User 가져오기
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found: " + username));
    }

    // 🔹 서버 등록: 현재 유저 기준
    @Transactional
    public ServerResponse createServer(ServerCreateRequest request) {
        User user = getCurrentUser();

        Server server = Server.builder()
                .user(user)                         // 👈 소유자 설정
                .name(request.getName())
                .description(request.getDescription())
                .status("UP")                      // 기본값
                .build();

        Server saved = serverRepository.save(server);
        return toDto(saved);
    }

    // 🔹 내 서버 목록 조회
    public List<ServerResponse> getServers() {
        User user = getCurrentUser();

        return serverRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 🔹 단일 서버 조회 (소유자 체크)
    public ServerResponse getServer(Long id) {
        User user = getCurrentUser();

        Server server = serverRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found: " + id));

        if (!server.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this server");
        }

        return toDto(server);
    }

    private ServerResponse toDto(Server server) {
        return ServerResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .description(server.getDescription())
                .status(server.getStatus())
                .createdAt(server.getCreatedAt())
                .lastCheckAt(server.getLastCheckAt())
                .build();
    }
}
