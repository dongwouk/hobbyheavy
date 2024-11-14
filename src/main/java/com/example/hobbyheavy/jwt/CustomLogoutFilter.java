package com.example.hobbyheavy.jwt;

import com.example.hobbyheavy.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 로그아웃 경로, POST 요청 확인
        String requestUri = request.getRequestURI();
        if (!"/logout".equals(requestUri) || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // refresh token 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    log.info("로그아웃 요청에 대한 refresh token 발견: {}", refresh);
                }
            }
        }

        // refresh token null check
        if (refresh == null) {
            log.warn("refresh token이 없어서 로그아웃을 처리할 수 없습니다.(이미 로그아웃이 된 상태)");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 refresh token으로 로그아웃 요청이 들어왔습니다. refresh token: {}", refresh);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            log.warn("잘못된 유형의 token이 제공되었습니다. 제공된 token: {}", refresh);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        if (!refreshRepository.existsByRefresh(refresh)) {
            log.warn("존재하지 않는 refresh token이 제공되었습니다. refresh token: {}", refresh);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        // 로그아웃 진행
        // Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);
        log.info("refresh token DB에서 제거 완료. refresh token: {}", refresh);

        // Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
        log.info("로그아웃 성공 및 refresh token 제거 완료.");
        System.out.println("로그아웃 성공.");
    }
}