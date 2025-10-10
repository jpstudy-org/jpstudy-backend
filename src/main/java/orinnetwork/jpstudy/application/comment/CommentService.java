package orinnetwork.jpstudy.application.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.comment.dto.CommentRequest;
import orinnetwork.jpstudy.application.comment.dto.CommentResponse;
import orinnetwork.jpstudy.domain.comment.Comment;
import orinnetwork.jpstudy.domain.comment.CommentRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.post.Post;
import orinnetwork.jpstudy.domain.post.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        Comment newComment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .member(member)
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return CommentResponse.from(savedComment);
    }
}
