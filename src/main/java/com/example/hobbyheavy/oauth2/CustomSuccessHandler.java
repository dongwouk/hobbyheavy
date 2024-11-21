package com.example.hobbyheavy.oauth2;

import com.example.hobbyheavy.entity.Refresh;
import com.example.hobbyheavy.jwt.JWTUtil;
import com.example.hobbyheavy.repository.RefreshRepository;
import com.example.hobbyheavy.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private final Long ACCESS_EXPIRED = 600000L;
    private final Long REFRESH_EXPIRED = 86400000L; // 86400000 24 시간

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String userId = customUserDetails.getUserId();

//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();

        // 사용자가 가진 모든 권한을 쉼표로 구분된 문자열로 병합하기 위한 방식
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtUtil.createJwt("access",userId, role,ACCESS_EXPIRED);
        String refreshToken = jwtUtil.createJwt("refresh", userId, role, REFRESH_EXPIRED);

        // Refresh token DB에 저장
        addRefreshEntity(userId, refreshToken, REFRESH_EXPIRED);

        response.addHeader("access", accessToken);
        response.addHeader("refresh", refreshToken);

        response.addCookie(CookieUtil.createCookie("access", accessToken));
        response.addCookie(CookieUtil.createCookie("refresh", refreshToken));
        response.getWriter().write("OAuth2 Login successful");
    }

    private void addRefreshEntity(String userId, String refresh, Long expiredMs){
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(expiredMs / 1000); // 밀리초를 초 단위로 변환
        Refresh refreshEntity = Refresh.builder()
                .userId(userId)
                .refresh(refresh)
                .expiration(expirationDate)
                .build();
        refreshRepository.save(refreshEntity);
    }

}
