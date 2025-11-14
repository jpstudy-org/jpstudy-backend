package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.LoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetConfirm;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetRequest;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.domain.member.LocalMember;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.email.EmailService;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    private final EmailService emailService;
    private static final String RESET_TOKEN_PREFIX = "RESET_TOKEN:";
    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 15;

    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
        final String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        final LocalMember newMember = new LocalMember(
                requestDto.getEmail(),
                requestDto.getUsername(),
                encryptedPassword,
                Role.USER
        );

        final Member savedMember = memberRepository.save(newMember);

        String accessToken = jwtProvider.createAccessToken(savedMember.getId(), savedMember.getRole());
        String refreshToken = jwtProvider.createRefreshToken(savedMember.getId());
        String userName = savedMember.getUsername();

        String redisKey = REFRESH_TOKEN_PREFIX + savedMember.getId().toString();
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                refreshTokenValidityMs,
                TimeUnit.MILLISECONDS
        );

        return new TokenResponseDto(accessToken, refreshToken, userName, refreshTokenValidityMs);
    }

    public TokenResponseDto login(LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.isEnabled()) {
            throw new IllegalArgumentException("탈퇴한 회원입니다");
        }

        Long memberId = userDetails.getMemberId();
        Role role = userDetails.getMember().getRole();
        String userName = userDetails.getUsername();

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

        return new TokenResponseDto(accessToken, refreshToken, userName, refreshTokenValidityMs);
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

    public TokenResponseDto reissueToken(String clientRefreshToken) {
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

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String userName = member.getUsername();
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        return new TokenResponseDto(newAccessToken, clientRefreshToken, userName, refreshTokenValidityMs);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String resetToken = UUID.randomUUID().toString();
        String redisKey = RESET_TOKEN_PREFIX + resetToken;

        redisTemplate.opsForValue().set(
                redisKey,
                member.getEmail(),
                RESET_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        emailService.sendPasswordResetLink(member.getEmail(), resetToken);
    }

    public void confirmPasswordReset(PasswordResetConfirm request) {
        String redisKey = RESET_TOKEN_PREFIX + request.getToken();

        String userEmail = redisTemplate.opsForValue().get(redisKey);

        if (userEmail == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (member instanceof LocalMember localMember) {
            localMember.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        } else {
            throw new IllegalArgumentException("비밀번호를 변경할 수 없는 타입입니다.");
        }

        redisTemplate.delete(redisKey);
    }
}
