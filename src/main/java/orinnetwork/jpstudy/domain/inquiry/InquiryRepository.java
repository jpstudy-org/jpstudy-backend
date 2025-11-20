package orinnetwork.jpstudy.domain.inquiry;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findByStatus(InquiryStatus status, Pageable pageable);

    List<Inquiry> findByMemberId(Long memberId);
}
