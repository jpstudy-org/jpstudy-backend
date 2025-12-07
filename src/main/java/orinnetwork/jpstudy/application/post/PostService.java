package orinnetwork.jpstudy.application.post;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;


    /**
     * 게시물 생성
     *
     * @param request       게시물 작성 DTO
     * @param memberId      사용자 ID
     * @return              저장 형태 반환
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
    public PostDetailResponse getPostById(Long id) {
        Post post = getActivePost(id);
        increaseViewCount(id);
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

        Page<Post> postPage = postRepository.findPostsWithMember(PostStatus.ACTIVE, PostType.NORMAL, pageable);
        return createPostSummaryResponse(postPage);
    }

    /**
     * 공지 조회
     * @return 최근 3개
     */
    public CustomPageResponse<PostSummaryResponse> getNotices(Pageable pageable) {

        Page<Post> postPage = postRepository.findPostsWithMember(PostStatus.ACTIVE, PostType.NOTICE, pageable);
        return createPostSummaryResponse(postPage);
    }

    /**
     * 공지 페이징 조회
     * @param pageable 페이지 번호
     * @return 해당 공지 게시물
     */
    public CustomPageResponse<PostSummaryResponse> getNoticePosts(Pageable pageable) {

        Page<Post> postPage = postRepository.findPostsWithMember(PostStatus.ACTIVE, PostType.NOTICE, pageable);
        return createPostSummaryResponse(postPage);
    }

    @Transactional
    public void syncViewCountsToDB() {
        ScanOptions options = ScanOptions.scanOptions().match("post:view:*").count(100).build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                String countStr = redisTemplate.opsForValue().get(key);
                if (countStr == null) continue;

                long count = Long.parseLong(countStr);
                Long postId = Long.parseLong(key.split(":")[2]);

                if (count > 0) {
                    postRepository.addViewCount(postId, count);
                }
                redisTemplate.delete(key);
            }
        } catch (Exception ignored) {
            // TODO: 로그 필요시 넣어야 함 (당장은 필요 여부를 모르겠음)
        }
    }

    // --- Private ---

    private CustomPageResponse<PostSummaryResponse> createPostSummaryResponse(Page<Post> postPage) {
        if (postPage.isEmpty()) {
            return new CustomPageResponse<>(postPage.map(post -> PostSummaryResponse.from(post, 0)));
        }

        List<String> keys = postPage.getContent().stream()
                .map(post -> "post:view:" + post.getId())
                .toList();

        List<String> redisValues = redisTemplate.opsForValue().multiGet(keys);

        Iterator<String> valueIterator = Objects.requireNonNull(redisValues).iterator();

        Page<PostSummaryResponse> responsePage = postPage.map(post -> {
            String redisValStr = valueIterator.hasNext() ? valueIterator.next() : null;
            int redisCount = (redisValStr != null) ? Integer.parseInt(redisValStr) : 0;

            int totalViewCount = post.getViewCount() + redisCount;

            return PostSummaryResponse.from(post, totalViewCount);
        });

        return new CustomPageResponse<>(responsePage);
    }

    private void increaseViewCount(Long postId) {
        String key = "post:view:" + postId;
        redisTemplate.opsForValue().increment(key);
    }

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