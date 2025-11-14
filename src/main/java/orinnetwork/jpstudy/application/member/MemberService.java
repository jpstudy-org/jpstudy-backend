package orinnetwork.jpstudy.application.member;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import orinnetwork.jpstudy.application.member.dto.MemberProfileResponse;
import orinnetwork.jpstudy.application.member.dto.MemberUpdateRequest;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    /**
     * 마이페이지 : 프로필 조회
     * @param userId 프로필 ID
     */
    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return MemberProfileResponse.from(member);
    }


    @Transactional
    public void updateProfile(Long userId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (StringUtils.hasText(request.getUsername())) {
            member.updateProfile(request.getUsername());
        }

        if (StringUtils.hasText(request.getLanguagePreference())) {
            member.updateLanguagePreference(request.getLanguagePreference());
        }
    }


    /**
     * 마이페이지 : 탈퇴
     * @param userId 탈퇴할 ID
     */
    @Transactional
    public void withdrawMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        member.withdraw();
    }

    @Transactional
    public void addExperience(Long userId, int experienceToAdd) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        member.addExperience(experienceToAdd);
        checkAndProcessLevelUp(member);

        memberRepository.save(member);
    }

    private void checkAndProcessLevelUp(Member member) {
        while (true) {
            long requiredExperience = calculateRequiredExperienceForNextLevel(member.getLevel());
            if (member.getExperience() >= requiredExperience) {
                member.levelUp(requiredExperience);
            }
            else break;
        }
    }

    private long calculateRequiredExperienceForNextLevel(int currentLevel) {
        if (currentLevel < 10) {
            return (currentLevel * 5L) + 10;
        }
        else if (currentLevel < 30) {
            return (currentLevel * 50L) + 50;
        }
        else if (currentLevel < 50) {
            return (currentLevel * 150L) + 1000;
        }
        else if (currentLevel < 100){
            return (currentLevel * 400L) + 5000;
        }
        else {
            return Long.MAX_VALUE;
        }
    }
}