package com.example.hobbyheavy.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 사용자 컨텍스트와 관련된 유틸리티 메서드를 제공하는 클래스.
 * 현재 로그인된 사용자의 정보를 쉽게 가져올 수 있는 기능을 제공합니다.
 */
@UtilityClass
public class UserContextUtil {

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * <p>Spring Security의 {@link SecurityContextHolder}를 사용하여 현재 컨텍스트의 인증 정보를 가져옵니다.</p>
     *
     * @return 현재 인증된 사용자의 ID (username)
     * @throws NullPointerException 인증 정보가 없을 경우 예외 발생
     */
    public String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
