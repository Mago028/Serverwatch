package com.serverwatch.config;

import com.serverwatch.entity.Server;
import com.serverwatch.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

    @Bean
    public CommandLineRunner initServers(ServerRepository serverRepository) {
        return args -> {

            if (serverRepository.count() == 0) {
                Server server = Server.builder()
                        .name("Main Server")
                        .description("메인 서버")
                        .status("GREEN")
                        .build();

                serverRepository.save(server);
                log.info("✅ Sample server inserted");
            }

            log.info("=== All Servers in DB ===");
            serverRepository.findAll()
                    .forEach(s -> log.info(
                            "Server(id={}, name={}, description={}, status={}, createdAt={}, lastCheckAt={})",
                            s.getId(),
                            s.getName(),
                            s.getDescription(),
                            s.getStatus(),
                            s.getCreatedAt(),
                            s.getLastCheckAt()
                    ));
        };
    }
}
