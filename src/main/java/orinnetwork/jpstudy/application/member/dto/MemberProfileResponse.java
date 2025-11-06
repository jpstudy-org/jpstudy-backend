package orinnetwork.jpstudy.application.member.dto;

import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.member.Member;

@Getter
public class MemberProfileResponse {

    private final String email;
    private final String username;
    private final int level;
    private final long experience;

    @Builder
    public MemberProfileResponse(String email, String username, int level, long experience) {
        this.email = email;
        this.username = username;
        this.level = level;
        this.experience = experience;
    }

    public static MemberProfileResponse from(Member member) {
        return MemberProfileResponse.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .level(member.getLevel())
                .experience(member.getExperience())
                .build();
    }
}