package com.serverwatch.config;

import com.serverwatch.security.JwtAuthFilter;
import com.serverwatch.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtUtil jwtUtil;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // CORS 허용 (프론트 React 개발 서버에서 오는 요청)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // JWT 방식이라 CSRF / 폼 로그인 / HTTP Basic 비활성화
                                .csrf(csrf -> csrf.disable())
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())

                                // 세션은 사용하지 않고, 매 요청마다 JWT로 인증
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // URL 별 인가 설정
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // ✅ 공개: 헬스체크 + Swagger + 루트
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                .requestMatchers("/", "/error").permitAll()

                                                // 로그인/회원가입 공개
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/api/auth/register")
                                                .permitAll()

                                                // 나머지는 JWT 필요
                                                .anyRequest().authenticated())

                                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                                .addFilterBefore(
                                                new JwtAuthFilter(jwtUtil),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * 프론트엔드(React)에서 오는 CORS 요청 허용 설정
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                // 허용할 Origin (개발용 React 서버)
                config.setAllowedOrigins(List.of(
                                "http://localhost:5173",
                                "http://localhost:5174"));

                // 허용할 HTTP 메서드
                config.setAllowedMethods(List.of(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

                // 허용할 요청 헤더
                config.setAllowedHeaders(List.of("*"));

                // 응답에서 노출할 헤더 (예: Authorization)
                config.setExposedHeaders(List.of("Authorization"));

                // 인증 정보(Authorization 헤더 등) 포함 허용
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}