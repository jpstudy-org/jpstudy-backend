package orinnetwork.jpstudy.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p "
            + "JOIN FETCH p.member m "
            + "LEFT JOIN FETCH p.category c "
            + "WHERE p.postStatus = :postStatus AND p.postType = :postType")
    Page<Post> findPostsWithMember(@Param("postStatus") PostStatus postStatus, @Param("postType") PostType postType, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + :count where p.id = :id")
    void addViewCount(@Param("id") Long id, @Param("count") long count);
}
