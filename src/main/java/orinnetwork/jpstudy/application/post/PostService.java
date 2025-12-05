package orinnetwork.jpstudy.application.post;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.post.dto.PostCreatedEvent;
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
import orinnetwork.jpstudy.infrastructure.config.RabbitConfig;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final RabbitTemplate rabbitTemplate;


    /**
     * 게시물 생성
     *
     * @param request 게시물 작성 DTO
     * @param memberId    사용자 ID
     * @return 저장 형태 반환
     */
    @Transactional
    public PostDetailResponse createPost(PostRequest request, Long memberId, String ipAddress) {
        Member author = getMember(memberId);
        Category category = resolveCategory(request.getCategoryId());

        Post newPost = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(author)
                .category(category)
                .postType(PostType.NORMAL)
                .postStatus(PostStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();

        Post savedPost = postRepository.save(newPost);

        publishPostCreatedEvent(savedPost.getId());

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
        Post post = getActivePost(id);
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

        validateOwnership(post, memberId);

        post.delete();
    }

    /**
     * 게시물 페이징 조회
     *
     * @param pageable 페이지 번호
     * @return 해당 페이지 게시물 [PostSummaryResponseDto]
     */
    public CustomPageResponse<PostSummaryResponse> getPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findByPostStatusAndPostType(PostStatus.ACTIVE, PostType.NORMAL, pageable);
        return new CustomPageResponse<>(postPage.map(PostSummaryResponse::from));
    }

    /**
     * 공지 조회
     * @return 최근 3개
     */
    public CustomPageResponse<PostSummaryResponse> getNotices(Pageable pageable) {

        Page<Post> postPage = postRepository.findByPostStatusAndPostType(PostStatus.ACTIVE, PostType.NOTICE, pageable);
        return new CustomPageResponse<>(postPage.map(PostSummaryResponse::from));
    }

    /**
     * 공지 페이징 조회
     * @param pageable 페이지 번호
     * @return 해당 공지 게시물
     */
    public CustomPageResponse<PostSummaryResponse> getNoticePosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findByPostStatusAndPostType(PostStatus.ACTIVE, PostType.NOTICE, pageable);
        return new CustomPageResponse<>(postPage.map(PostSummaryResponse::from));
    }

    // --- Private ---

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND));
    }

    private Post getActivePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new CustomException(ErrorCode.POST_NOT_ACTIVE);
        }
        return post;
    }

    private void validateOwnership(Post post, Long memberId) {
        if (!post.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.POST_NOT_OWNER);
        }
    }

    private void publishPostCreatedEvent(Long postId) {
        PostCreatedEvent event = new PostCreatedEvent(postId);

        rabbitTemplate.convertAndSend(
                RabbitConfig.POST_EXCHANGE_NAME,
                RabbitConfig.POST_CREATED_ROUTING_KEY,
                event
        );
    }
}