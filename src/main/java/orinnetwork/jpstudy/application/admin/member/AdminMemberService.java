package orinnetwork.jpstudy.application.admin.member;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.member.dto.MemberAdminResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.MemberStatus;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public CustomPageResponse<MemberAdminResponse> getMembers(String keyword, MemberStatus status, Pageable pageable) {
        Page<Member> memberPage = memberRepository.searchMembers(keyword, status, pageable);

        Page<MemberAdminResponse> responses = memberPage.map(MemberAdminResponse::from);

        return new CustomPageResponse<>(responses);
    }

    public void banMember(Long memberId, Integer days) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (days == null || days == 0) {
            member.ban(null);
        } else {
            member.ban(LocalDateTime.now().plusDays(days));
        }
    }

    public void unbanMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.unban();
    }
}
