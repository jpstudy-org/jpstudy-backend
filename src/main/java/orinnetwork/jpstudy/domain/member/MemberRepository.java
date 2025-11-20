package orinnetwork.jpstudy.domain.member;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM OauthMember m WHERE m.provider = :provider AND m.providerId = :providerId")
    Optional<OauthMember> findByProviderAndProviderId(@Param("provider") String provider,
                                                      @Param("providerId") String providerId);

    @Query("SELECT m FROM Member m WHERE "
            + "(:keyword IS NULL OR :keyword = '' OR m.email LIKE %:keyword% OR m.username LIKE %:keyword%) "
            + "AND (:status IS NULL OR m.status = :status)")
    Page<Member> searchMembers(
            @Param("keyword") String keyword,
            @Param("status") MemberStatus status,
            Pageable pageable
    );
}