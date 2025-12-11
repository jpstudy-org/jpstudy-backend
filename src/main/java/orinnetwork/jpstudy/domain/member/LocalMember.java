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

    private LocalMember(String email, String username, String password, Role role) {
        super(email, username, role);
        this.password = password;
    }

    public static LocalMember join(String email, String username, String encodePassword) {
        return new LocalMember(email, username, encodePassword, Role.USER);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
