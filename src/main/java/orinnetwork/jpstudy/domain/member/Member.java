package orinnetwork.jpstudy.domain.member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "auth_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "username")
    private String username;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private long experience = 0;

    @Column(length = 5, nullable = false)
    private String languagePreference = "en";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private MemberGrade grade;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTitle> memberTitles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    private LocalDateTime banExpiresAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Member(String email, String username, Role role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public void ban(LocalDateTime expiresAt) {
        this.banExpiresAt = expiresAt;
        if (expiresAt == null) {
            this.status = MemberStatus.BANNED;
        } else {
            this.status = MemberStatus.SUSPENDED;
        }
    }

    public void unban() {
        this.status = MemberStatus.ACTIVE;
        this.banExpiresAt = null;
    }

    public boolean isAccountNonLocked() {
        if (this.status == MemberStatus.BANNED) {
            return false;
        }
        if (this.status == MemberStatus.SUSPENDED) {
            return LocalDateTime.now().isAfter(this.banExpiresAt);
        }
        return true;
    }

    public void updateProfile(String username) {
        this.username = username;
    }

    public void updateLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    /**
     *
     * @param experienceToAdd 경험치 추가 로직
     */
    public void addExperience(int experienceToAdd) {
        if (experienceToAdd > 0) {
            this.experience += experienceToAdd;
        }
    }

    /**
     *
     * @param requiredExperience 레벨업 처리 로직
     */
    public void levelUp(long requiredExperience) {
        this.level++;
        this.experience -= requiredExperience;
    }

    public abstract String getPassword();

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }
}