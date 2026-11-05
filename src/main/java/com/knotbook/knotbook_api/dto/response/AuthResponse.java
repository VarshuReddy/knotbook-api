package com.knotbook.knotbook_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;

    private String accessToken;
    private String refreshToken;

    private String tokenType;
    private long expiresIn;
}
