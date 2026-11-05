package com.knotbook.knotbook_api.service;

import com.knotbook.knotbook_api.dto.request.LoginRequest;
import com.knotbook.knotbook_api.dto.request.RegisterRequest;
import com.knotbook.knotbook_api.enity.User;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock  //creates fake dependency
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks  //creates AUthservice and injects its dependencies
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "Varshini",
                "varshini@example.com",
                "encodedPassword"
        );
    }

    @Test
    void register_shouldSaveUser_whenEmailIsNew() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Varshini");
        request.setEmail("varshini@example.com");
        request.setPassword("KnotBook123");

        // what it should return when a particular method is called
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("Varshini", response.getName());
        assertEquals("varshini@example.com", response.getEmail());

        //check if particualr method is called
        verify(passwordEncoder).encode("KnotBook123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Varshini");
        request.setEmail("varshini@example.com");
        request.setPassword("KnotBook123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                ApiException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {

        LoginRequest request = new LoginRequest();
        request.setEmail("varshini@example.com");
        request.setPassword("KnotBook123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("access-token");

        when(jwtService.getAccessExpiration())
                .thenReturn(900L);

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn("refresh-token");

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(900L, response.getExpiresIn());

        verify(passwordEncoder).matches(
                "KnotBook123",
                "encodedPassword"
        );
    }

    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("varshini@example.com");
        request.setPassword("WrongPassword");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).thenReturn(false);

        assertThrows(
                ApiException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateAccessToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    @Test
    void login_shouldThrowException_whenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("KnotBook123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                ApiException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
