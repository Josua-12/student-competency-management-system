package com.competency.SCMS.security;

import com.competency.SCMS.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security가 인식할 수 있는 사용자 정보 래퍼
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 사용자 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자명 반환 (학번 사용)
    @Override
    public String getUsername() {
        return user.getUserNum().toString();
    }

    // 계정 비활성화 확인 (true = 활성)
    @Override
    public boolean isAccountNonExpired() {
        return user.getDeletedAt() == null;  // 삭제되지 않았으면 활성
    }

    // 계정 잠금 확인 (true = 활성)
    @Override
    public boolean isAccountNonLocked() {
        return !user.getLocked();
    }

    // 비밀번호 만료 여부 확인
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부 확인
    @Override
    public boolean isEnabled() {
        return user.getDeletedAt() == null && !user.getLocked();
    }
}
