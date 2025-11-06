package orinnetwork.jpstudy.application.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.comment.dto.CommentRequest;
import orinnetwork.jpstudy.application.comment.dto.CommentResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.comment.Comment;
import orinnetwork.jpstudy.domain.comment.CommentRepository;
import orinnetwork.jpstudy.domain.comment.CommentStatus;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.post.Post;
import orinnetwork.jpstudy.domain.post.PostRepository;
import orinnetwork.jpstudy.domain.post.PostStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long memberId, String ipAddress) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 찾을 수 없습니다."));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new IllegalArgumentException("삭제되거나 비공개된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        Comment newComment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .member(member)
                .ipAddress(ipAddress)
                .commentStatus(CommentStatus.ACTIVE)
                .build();

        Comment savedComment = commentRepository.save(newComment);

        post.increaseCommentCount();

        return CommentResponse.from(savedComment);
    }

    public CustomPageResponse<CommentResponse> getComments(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 찾을 수 없습니다."));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new IllegalArgumentException("삭제되거나 비공개된 게시글입니다.");
        }

        Page<Comment> commentPage = commentRepository.findByPost_IdAndCommentStatus(
                postId,
                CommentStatus.ACTIVE,
                pageable
        );

        Page<CommentResponse> responses = commentPage.map(CommentResponse::from);

        return new CustomPageResponse<>(responses);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        comment.delete();

        Post post = comment.getPost();
        post.decreaseCommentCount();
    }
}