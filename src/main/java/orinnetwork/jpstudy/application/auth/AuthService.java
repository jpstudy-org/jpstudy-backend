package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.LoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.domain.member.LocalMember;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.Role;
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

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto login(LoginRequestDto  requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();
        Role role = userDetails.getMember().getRole();

        String accessToken = jwtProvider.createAccessToken(memberId, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        return new TokenResponseDto(accessToken, refreshToken);
    }
}
