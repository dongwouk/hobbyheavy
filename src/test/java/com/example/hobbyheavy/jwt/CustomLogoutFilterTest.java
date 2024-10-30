package com.example.hobbyheavy.jwt;

import com.example.hobbyheavy.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CustomLogoutFilterTest {

    @Mock
    private RefreshRepository refreshRepository;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CustomLogoutFilter customLogoutFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilter_ShouldContinueChain_WhenUriIsNotLogout() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-other-uri");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        customLogoutFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_ShouldReturnBadRequest_WhenRefreshTokenIsExpired() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/logout");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("refresh", "expiredToken"));

        // Stubbing
        doThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"))
                .when(jwtUtil).isExpired("expiredToken");

        // When
        customLogoutFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(request, response);
        assert response.getStatus() == MockHttpServletResponse.SC_BAD_REQUEST;
    }

    @Test
    void doFilter_ShouldLogoutSuccessfully_WhenValidRefreshTokenIsProvided() throws IOException, ServletException {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/logout");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("refresh", "validRefreshToken"));

        // Stubbing
        when(jwtUtil.isExpired("validRefreshToken")).thenReturn(false);
        when(jwtUtil.getCategory("validRefreshToken")).thenReturn("refresh");
        when(refreshRepository.existsByRefresh("validRefreshToken")).thenReturn(true);

        // When
        customLogoutFilter.doFilter(request, response, filterChain);

        // Then
        verify(refreshRepository, times(1)).deleteByRefresh("validRefreshToken");
        assert response.getStatus() == MockHttpServletResponse.SC_OK;
    }
}
