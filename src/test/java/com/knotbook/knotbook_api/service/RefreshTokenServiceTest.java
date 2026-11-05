package com.knotbook.knotbook_api.service;

import com.knotbook.knotbook_api.enity.RefreshToken;
import com.knotbook.knotbook_api.enity.User;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;
    private String rawToken;

    @BeforeEach
    void setUp() throws Exception {

        user = new User(
                "Varshini",
                "varshini@example.com",
                "encodedPassword"
        );

        rawToken = "test-refresh-token";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        String hash = HexFormat.of().formatHex(
                digest.digest(rawToken.getBytes(StandardCharsets.UTF_8))
        );

        refreshToken = new RefreshToken(
                user,
                hash,
                Instant.now().plusSeconds(3600)
        );
    }

    @Test
    void validateAndRotate_shouldRevokeOldToken_whenTokenIsValid()
            throws Exception {

        String hash = hashToken(rawToken);

        when(refreshTokenRepository.findByTokenHashForUpdate(hash))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.validateAndRotate(rawToken);

        assertNotNull(result);
        assertTrue(result.isRevoked());

    }

    @Test
    void validateAndRotate_shouldReject_whenTokenDoesNotExist() {

        when(refreshTokenRepository.findByTokenHashForUpdate(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ApiException.class,
                () -> refreshTokenService.validateAndRotate(rawToken)
        );
    }

    @Test
    void validateAndRotate_shouldReject_whenTokenIsAlreadyRevoked()
            throws Exception {

        refreshToken.revoke();

        when(refreshTokenRepository.findByTokenHashForUpdate(anyString()))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                ApiException.class,
                () -> refreshTokenService.validateAndRotate(rawToken)
        );
    }

    @Test
    void revoke_shouldRevokeValidToken() throws Exception {

        when(refreshTokenRepository.findByTokenHashForUpdate(anyString()))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.revoke(rawToken);

        assertTrue(refreshToken.isRevoked());
    }

    private String hashToken(String token) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        return HexFormat.of().formatHex(
                digest.digest(token.getBytes(StandardCharsets.UTF_8))
        );
    }
}