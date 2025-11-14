package orinnetwork.jpstudy.infrastructure.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import orinnetwork.jpstudy.domain.member.Member;
import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // 인증된 사용자 엔티티를 포함합니다.
    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 역할(Role)을 기반으로 권한 목록을 반환합니다.
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    @Override
    public String getPassword() {
        // 인증 시 비밀번호 비교를 위해 Member의 비밀번호를 반환합니다.
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        // 인증 시 사용자 이름 (여기서는 닉네임이나 이메일, Member 엔티티의 username을 사용)을 반환합니다.
        return member.getUsername();
    }

    // 계정 만료, 잠금, 비밀번호 만료, 활성화 여부는 모두 true로 설정합니다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return member.getDeletedAt() != null;
    }

    // 이 메서드를 통해 엔티티의 ID를 바로 가져올 수 있습니다. (인증 후 활용 목적)
    public Long getMemberId() {
        return member.getId();
    }
}
