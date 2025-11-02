package orinnetwork.jpstudy.application.admin.auth;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.admin.auth.dto.AdminLoginRequest;
import orinnetwork.jpstudy.application.admin.auth.dto.AdminTokenResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AdminAuthservice {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    public AdminTokenResponse login(AdminLoginRequest requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();
        Role role = userDetails.getMember().getRole();

        if (role != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }


        String accessToken = jwtProvider.createAccessToken(memberId, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        String redisKey = REFRESH_TOKEN_PREFIX + memberId;
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                refreshTokenValidityMs,
                TimeUnit.MILLISECONDS
        );

        return new AdminTokenResponse(accessToken, refreshToken);
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return;
        }

        Long memberId = userDetails.getMemberId();

        String redisKey = REFRESH_TOKEN_PREFIX + memberId.toString();
        if (redisTemplate.opsForValue().get(redisKey) != null) {
            redisTemplate.delete(redisKey);
        }
    }

    public AdminTokenResponse reissueToken(String clientRefreshToken) {
        if (!jwtProvider.isValidToken(clientRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 RefreshToken 입니다.");
        }

        Long memberId = jwtProvider.getUserId(clientRefreshToken);

        String redisKey = REFRESH_TOKEN_PREFIX + memberId.toString();
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (storedRefreshToken == null) {
            throw new IllegalArgumentException("로그아웃된 사용자입니다. 다시 로그인하세요.");
        }

        if (!storedRefreshToken.equals(clientRefreshToken)) {
            throw new IllegalArgumentException("토큰이 일치하지 않습니다. 비정상적인 접근입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 회원을 찾을 수 없습니다."));

        if (member.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        return new AdminTokenResponse(newAccessToken, clientRefreshToken);
    }
}
