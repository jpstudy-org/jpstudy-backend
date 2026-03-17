package orinnetwork.jpstudy.application.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.member.Member;

@Getter
@Schema(description = "사용자 프로필 정보 응답 DTO (내 정보 조회)")
public class MemberProfileResponse {

    @Schema(description = "사용자 이메일 (계정 식별자)")
    private final String email;

    @Schema(description = "사용자 이름 또는 닉네임")
    private final String username;

    @Schema(description = "현재 학습 레벨")
    private final int level;

    @Schema(description = "총 누적 경험치")
    private final long experience;

    @Schema(description = "언어 설정 (kr, en, jp)")
    private final String languagePreference;

    @Builder
    public MemberProfileResponse(String email, String username, int level, long experience,
                                 String languagePreference) {
        this.email = email;
        this.username = username;
        this.level = level;
        this.experience = experience;
        this.languagePreference = languagePreference;
    }

    public static MemberProfileResponse from(Member member) {
        return MemberProfileResponse.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .level(member.getLevel())
                .experience(member.getExperience())
                .languagePreference(member.getLanguagePreference())
                .build();
    }
}