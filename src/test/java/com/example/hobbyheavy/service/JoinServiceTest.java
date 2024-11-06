package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.JoinDTO;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class JoinServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private JoinService joinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void joinProcess_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Given
        String username = "testUser";
        JoinDTO joinDTO = JoinDTO.builder()
                .userId(username)
                .password("password")
                .email("test@example.com")
                .build();

        // Stubbing - When 'existsByUsername' is called with 'username', return true
        when(userRepository.existsByUserId(username)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> joinService.joinProcess(joinDTO));
        verify(userRepository, times(1)).existsByUserId(username);
    }

    @Test
    void joinProcess_ShouldSaveUser_WhenUsernameDoesNotExist() {
        // Given
        String username = "newUser";
        String password = "password";
        String email = "new@example.com";
        JoinDTO joinDTO = JoinDTO.builder()
                .userId(username)
                .password(password)
                .email(email)
                .build();

        // Stubbing
        when(userRepository.existsByUserId(username)).thenReturn(false);
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");

        // When
        joinService.joinProcess(joinDTO);

        // Then
        verify(userRepository, times(1)).existsByUserId(username);
        verify(bCryptPasswordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
