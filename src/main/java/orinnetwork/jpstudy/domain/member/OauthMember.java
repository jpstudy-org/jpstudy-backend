package orinnetwork.jpstudy.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor
public class OauthMember extends Member {

    @Column()
    private String provider;

    @Column()
    private String providerId;

    public OauthMember(String email, String username, Role role, String provider, String providerId) {
        super(email, username, role);
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public String getPassword() {
        return "";
    }
}
