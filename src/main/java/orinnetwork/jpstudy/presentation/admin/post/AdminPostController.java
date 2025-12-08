package orinnetwork.jpstudy.presentation.admin.post;

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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.post.AdminPostService;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.post.PostService;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.application.post.dto.PostSummaryResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;

@Tag(name = "Admin - Post", description = "관리자: 게시글 및 공지사항 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final AdminPostService adminPostService;
    private final PostService postService;

    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 생성합니다.")
    @PostMapping("/notices")
    public ResponseEntity<PostDetailResponse> createNotice(
            @Valid @RequestBody PostRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = IpUtil.getClientIp(httpServletRequest);

        PostDetailResponse response = adminPostService.createNotice(
                request,
                userDetails.getMemberId(),
                ipAddress
        );

        return ResponseEntity
                .created(URI.create("/api/posts/" + response.getPostId()))
                .body(response);
    }

    @Operation(summary = "공지사항 목록 페이지 조회", description = "모든 공지사항 목록을 페이지네이션하여 조회합니다.")
    @GetMapping("/notices")
    public ResponseEntity<CustomPageResponse<PostSummaryResponse>> getNoticePage(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable
    ) {
        CustomPageResponse<PostSummaryResponse> responsePage = postService.getNoticePosts(pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "공지사항 수정", description = "특정 공지사항의 내용을 수정합니다.")
    @PutMapping("/notices/{postId}")
    public ResponseEntity<PostDetailResponse> updateNotice(
            @Parameter(description = "수정할 공지사항 ID")
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request
    ) {
        PostDetailResponse response = adminPostService.updateNotice(postId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글/공지사항 삭제", description = "특정 ID의 게시글 또는 공지사항을 강제로 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID")
            @PathVariable Long postId
    ) {
        adminPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
