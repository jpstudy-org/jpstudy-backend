package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequestDto;
import orinnetwork.jpstudy.domain.member.LocalMember;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Long signUp(SignUpRequestDto requestDto) {
        final String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        final LocalMember newMember = new LocalMember(
                requestDto.getEmail(),
                requestDto.getUsername(),
                encryptedPassword,
                Role.USER
        );

        final Member savedMember = memberRepository.save(newMember);

        return savedMember.getId();
    }
}
