package com.knotbook.knotbook_api.service;


import com.knotbook.knotbook_api.dto.request.LoginRequest;
import com.knotbook.knotbook_api.dto.request.RefreshTokenRequest;
import com.knotbook.knotbook_api.dto.request.RegisterRequest;
import com.knotbook.knotbook_api.dto.response.AuthResponse;
import com.knotbook.knotbook_api.dto.response.RegisterResponse;
import com.knotbook.knotbook_api.enity.RefreshToken;
import com.knotbook.knotbook_api.enity.User;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor  // constructor for final fields
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // injected from SecurityConfig
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest){
        String email = registerRequest.getEmail().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(email)){
            throw new ApiException(HttpStatus.CONFLICT,"Email already exists");
        }

        //We save the BCrypt hash, not the original password.
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User( registerRequest.getName().trim(),
                email,
                encodedPassword);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                "Registration successful"
        );
    }

    /**
     * @param loginRequest
     * @return ResponseAuth
     *
     *
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest){
        String email = loginRequest.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);

        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())){
            throw invalidCredentials();
        }
        return  generateAuthResponse(user);
    }

    /**
     *Android sends the refresh token.
     * RefreshTokenService validates it.
     * The old token gets revoked.
     * We retrieve the associated user.
     * generateAuthResponse() creates a new access token.
     * A new refresh token is generated and saved.
     * Android receives both replacement tokens.
     *
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshToken oldToken =
                refreshTokenService.validateAndRotate(
                        request.getRefreshToken()
                );

        User user = oldToken.getUser();

        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {

        refreshTokenService.revoke(request.getRefreshToken());
    }

    /**
     *
     * @param user
     * @return AuthRespone
     *  JwtService --> Calls JwtEncoder --> Access JWT
     *  RefreshTokenService --> Calls Repository --> Refresh token
     */
    private AuthResponse generateAuthResponse(User user) {

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpiration()
        );
    }

    private ApiException invalidCredentials() {

        return new ApiException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

}
