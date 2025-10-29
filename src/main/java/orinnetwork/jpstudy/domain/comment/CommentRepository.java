package orinnetwork.jpstudy.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPost_IdAndCommentStatus(Long postId, CommentStatus status, Pageable pageable);
}
