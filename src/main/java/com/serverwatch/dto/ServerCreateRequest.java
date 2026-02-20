package com.serverwatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServerCreateRequest {

    @NotBlank
    private String name;          // 서버 이름

    private String description;   // 설명 (선택)
}
