package orinnetwork.jpstudy.application.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.application.post.dto.PostSummaryResponse;
import orinnetwork.jpstudy.domain.category.Category;
import orinnetwork.jpstudy.domain.category.CategoryRepository;
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
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;


    /**
     * 게시물 생성
     *
     * @param postRequest 게시물 작성 DTO
     * @param memberId    사용자 ID
     * @return 저장 형태 반환
     */
    @Transactional
    public PostDetailResponse createPost(PostRequest postRequest, Long memberId, String ipAddress) {
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Category category = null;
        Long categoryId = postRequest.getCategoryId();

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND));
        }

        Post newPost = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .member(author)
                .category(category)
                .postType(PostType.NORMAL)
                .postStatus(PostStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();

        Post savedPost = postRepository.save(newPost);

        return PostDetailResponse.from(savedPost);
    }

    /**
     * 게시물 조회 (단일)
     *
     * @param id 게시물 ID
     * @return 게시물 내용
     */
    @Transactional
    public PostDetailResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new CustomException(ErrorCode.POST_NOT_ACTIVE);
        }

        post.increaseViewCount();

        return PostDetailResponse.from(post);
    }

    /**
     * 게시물 삭제
     *
     * @param id       게시물 ID
     * @param memberId 사용자 ID
     */
    @Transactional
    public void deletePost(Long id, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.POST_NOT_OWNER);
        }

        post.delete();
    }

    /**
     * 게시물 페이징 조회
     *
     * @param pageable 페이지 번호
     * @return 해당 페이지 게시물 [PostSummaryResponseDto]
     */
    public CustomPageResponse<PostSummaryResponse> getPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findByPostStatus(PostStatus.ACTIVE, pageable);
        Page<PostSummaryResponse> responsePage = postPage.map(PostSummaryResponse::from);

        return new CustomPageResponse<>(responsePage);
    }
}