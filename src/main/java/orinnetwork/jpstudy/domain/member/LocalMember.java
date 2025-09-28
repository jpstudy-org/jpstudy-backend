package orinnetwork.jpstudy.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("LOCAL")
@NoArgsConstructor
public class LocalMember extends Member {

    @Column(name = "password")
    private String password;

    public LocalMember(String email, String username, String password, Role role) {
        super(email, username, role);
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
