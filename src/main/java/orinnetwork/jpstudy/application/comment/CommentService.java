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
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

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
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new CustomException(ErrorCode.POST_NOT_ACTIVE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new CustomException(ErrorCode.POST_NOT_ACTIVE);
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
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNER);
        }

        comment.delete();

        Post post = comment.getPost();
        post.decreaseCommentCount();
    }
}