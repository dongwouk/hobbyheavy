package com.example.hobbyheavy.auth;

import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.type.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security의 UserDetails 구현 클래스.
 * 인증된 사용자 정보를 제공하며, Spring Security가 이를 사용하여 인증 및 권한 부여를 처리.
 */
@RequiredArgsConstructor // final 필드를 초기화하는 생성자를 자동으로 생성
public class CustomUserDetails implements UserDetails {

    private final User user; // 사용자 정보를 담고 있는 엔티티

    /**
     * 사용자 권한 정보를 반환.
     * Spring Security가 인증 및 권한 부여 시 사용.
     *
     * @return 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User 엔티티의 UserRole 필드에서 권한 정보 추출
        Set<UserRole> userRoles = user.getUserRole(); // 열거형 Role 사용
        // 권한 정보를 SimpleGrantedAuthority 객체로 변환
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // 권한 이름으로 변환
                .collect(Collectors.toSet()); // Set<GrantedAuthority>로 변환
    }

    /**
     * 사용자의 비밀번호를 반환.
     *
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 고유 ID를 반환.
     *
     * @return 사용자 ID
     */
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환.
     *
     * @return 항상 true (만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부를 설정할 필요가 있다면 이 메서드를 수정
    }

    /**
     * 계정이 잠기지 않았는지 여부를 반환.
     *
     * @return 항상 true (잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부를 설정할 필요가 있다면 이 메서드를 수정
    }

    /**
     * 자격 증명(비밀번호)이 만료되지 않았는지 여부를 반환.
     *
     * @return 항상 true (만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부를 설정할 필요가 있다면 이 메서드를 수정
    }

    /**
     * 계정이 활성화되었는지 여부를 반환.
     *
     * @return 항상 true (활성화 상태)
     */
    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부를 설정할 필요가 있다면 이 메서드를 수정
    }
}
