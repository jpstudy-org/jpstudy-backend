package orinnetwork.jpstudy.domain.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM OauthMember m WHERE m.provider = :provider AND m.providerId = :providerId")
    Optional<OauthMember> findByProviderAndProviderId(@Param("provider") String provider,
                                                      @Param("providerId") String providerId);
}