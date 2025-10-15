package orinnetwork.jpstudy.application.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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