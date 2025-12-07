package orinnetwork.jpstudy.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.post.id = :postId AND c.commentStatus = :status")
    Page<Comment> findByPost_IdAndCommentStatus(@Param("postId") Long postId, @Param("status") CommentStatus status, Pageable pageable);
}
