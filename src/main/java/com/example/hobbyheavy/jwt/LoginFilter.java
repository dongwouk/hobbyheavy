package com.example.hobbyheavy.jwt;

import com.example.hobbyheavy.entity.RefreshEntity;
import com.example.hobbyheavy.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private final Long ACCESS_EXPIRED = 600000L;
    private final Long REFRESH_EXPIRED = 86400000L;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        String username = null;
        String password = null;

        try {
            // 요청 본문을 파싱하기 위해 ObjectMapper 사용
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);

            username = requestMap.get("username");
            password = requestMap.get("password");
        } catch (IOException e) {
            throw new AuthenticationException("요청 데이터를 읽을 수 없습니다.") {
            };
        }

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password); // 매개변수 null 추가???

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }
    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        // user info
        String username = authentication.getName();

        // 사용자가 가진 모든 권한을 쉼표로 구분된 문자열로 병합하기 위한 방식
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 생성
        // 600000L 10분 , 86400000L 24시간
        String access = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRED);
        String refresh = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRED);

        // Refresh token DB에 저장
        addRefreshEntity(username, refresh, REFRESH_EXPIRED);

        // 응답 본문에 토큰을 JSON 형태로 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형태로 응답
        String jsonResponse = String.format("{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}", access, refresh);
        response.getWriter().write(jsonResponse);

        // 응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs){
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(expiredMs.toString())
                .build();
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        // https 통신시 setSecure 설정
        // cookie.setSecure(true);
        // cookie 적용될 범위 설정
        //  cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;

    }
}
