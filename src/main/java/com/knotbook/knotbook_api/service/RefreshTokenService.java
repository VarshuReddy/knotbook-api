package com.knotbook.knotbook_api.service;

import com.knotbook.knotbook_api.enity.RefreshToken;
import com.knotbook.knotbook_api.enity.User;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     *
     * @param user
     * @return token
     * Generate 32 cryptographically random bytes.
     * Convert them into a URL-safe string.
     * Hash that string using SHA-256.
     * Store only the hash in MySQL associated with that user.
     * Return the original raw token to Android.
     */
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken(
                user,
                hashToken(rawToken),
                Instant.now().plusSeconds(refreshExpiration)
        );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    /**
     * It does not itself generate the replacement token.
     *  It validates and revokes the old token, then returns its associated entity.
     *
     * @param rawToken
     * @return refreshToken
     *
     * the received raw refershtoken and Hash it again.
     * Search MySQL using the hash.
     * Check expiry and revocation.
     * Revoke the old token.
     * Allow the AuthService to issue a replacement.
     *
     */

    @Transactional
    public RefreshToken validateAndRotate(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(this::invalidToken);

        if (oldToken.isRevoked() || oldToken.isExpired()) {
            throw invalidToken();
        }

        oldToken.revoke();

        return oldToken;
    }

    public void revoke(String rawToken) {

        RefreshToken token = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(this::invalidToken);

        if (token.isRevoked() || token.isExpired()) {
            throw invalidToken();
        }

        token.revoke();
    }

    // SHA-256 to hash the string
    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return java.util.HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash refresh token", ex
            );
        }
    }

    private ApiException invalidToken() {
        return new ApiException(
                HttpStatus.UNAUTHORIZED,
                "Invalid or expired refresh token"
        );
    }

}
