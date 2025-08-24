package orinnetwork.jpstudy.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor
public class OauthMember extends Member {

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String providerId;

    public OauthMember(String email, String username, Role role, String provider, String providerId) {
        super(email, username, role);
        this.provider = provider;
        this.providerId = providerId;
    }
}
