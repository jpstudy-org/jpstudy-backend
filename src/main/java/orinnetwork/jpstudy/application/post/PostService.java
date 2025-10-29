package orinnetwork.jpstudy.application.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostSummaryResponse;
import orinnetwork.jpstudy.domain.category.Category;
import orinnetwork.jpstudy.domain.category.CategoryRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.post.Post;
import orinnetwork.jpstudy.domain.post.PostRepository;
import orinnetwork.jpstudy.domain.post.PostStatus;
import orinnetwork.jpstudy.domain.post.PostType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;


    /**
     * 게시물 생성
     * @param postRequest 게시물 작성 DTO
     * @param memberId 사용자 ID
     * @return 저장 형태 반환
     */
    @Transactional
    public PostDetailResponse createPost(PostRequest postRequest, Long memberId, String ipAddress) {
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        Category category = null;
        Long categoryId = postRequest.getCategoryId();

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리를 찾을 수 없습니다."));
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
     * @param id 게시물 ID
     * @return 게시물 내용
     */
    @Transactional
    public PostDetailResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if (post.getPostStatus() != PostStatus.ACTIVE) {
            throw new IllegalArgumentException("조회할 수 없는 게시글입니다.");
        }

        post.increaseViewCount();

        return PostDetailResponse.from(post);
    }

    /**
     * 게시물 삭제
     * @param id 게시물 ID
     * @param memberId 사용자 ID
     */
    @Transactional
    public void deletePost(Long id, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        post.delete();
    }

    /**
     * 게시물 페이징 조회
     * @param pageable 페이지 번호
     * @return 해당 페이지 게시물 [PostSummaryResponseDto]
     */
    public Page<PostSummaryResponse> getPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findByPostStatus(PostStatus.ACTIVE, pageable);

        return postPage.map(PostSummaryResponse::from);
    }
}