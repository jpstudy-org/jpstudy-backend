package orinnetwork.jpstudy.application.admin.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.post.Post;
import orinnetwork.jpstudy.domain.post.PostRepository;
import orinnetwork.jpstudy.domain.post.PostStatus;
import orinnetwork.jpstudy.domain.post.PostType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostDetailResponse createNotice(PostRequest request, Long memberId, String ipAddress) {
        Member admin = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post notice = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(admin)
                .category(null)
                .postType(PostType.NOTICE)
                .postStatus(PostStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();

        Post savedNotice = postRepository.save(notice);

        return PostDetailResponse.from(savedNotice);
    }

    public PostDetailResponse updateNotice(Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getPostType() != PostType.NOTICE) {
            throw new CustomException(ErrorCode.POST_NOT_OWNER);
        }

        post.update(request.getTitle(), request.getContent(), null, PostStatus.ACTIVE);

        return PostDetailResponse.from(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.delete();
    }
}
