package orinnetwork.jpstudy.presentation.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.post.PostService;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.application.post.dto.PostSummaryResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;

@Tag(name = "Post API", description = "게시글(공지 포함) 생성, 조회, 삭제 관리")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "새 게시글 작성", description = "새로운 게시글을 작성하고 생성된 리소스를 반환합니다.")
    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(
            @Valid @RequestBody PostRequest requestDto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {

        Long memberId = userDetails.getMemberId();
        String ipAddress = IpUtil.getClientIp(request);

        PostDetailResponse response = postService.createPost(requestDto, memberId, ipAddress);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getPostId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 ID의 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(
            @Parameter(description = "게시글 ID")
            @PathVariable Long id
    ) {
        PostDetailResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "게시글 삭제", description = "특정 ID의 게시글을 삭제합니다. 작성자만 삭제 가능합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID")
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        postService.deletePost(id, memberId);

        return ResponseEntity.noContent().build();
    }

    //--------------------------------------------------
    // List Endpoints
    //--------------------------------------------------

    @Operation(summary = "일반 게시글 목록 조회", description = "모든 일반 게시글을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<PostSummaryResponse>> getPosts(
            @ParameterObject
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        CustomPageResponse<PostSummaryResponse> responsePage = postService.getPosts(pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "최신 공지사항 목록 조회", description = "가장 최신 공지사항 3개를 조회합니다.")
    @GetMapping("/notices/recent")
    public ResponseEntity<CustomPageResponse<PostSummaryResponse>> getRecentNotices(
            @ParameterObject
            @PageableDefault(size = 3, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable
    ) {
        CustomPageResponse<PostSummaryResponse> notices = postService.getNotices(pageable);
        return ResponseEntity.ok(notices);
    }

    @Operation(summary = "공지사항 페이지 조회", description = "모든 공지사항을 페이지네이션하여 조회합니다.")
    @GetMapping("/notices")
    public ResponseEntity<CustomPageResponse<PostSummaryResponse>> getNoticePage(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {
        CustomPageResponse<PostSummaryResponse> responsePage = postService.getNoticePosts(pageable);
        return ResponseEntity.ok(responsePage);
    }
}