package orinnetwork.jpstudy.presentation.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.comment.CommentService;
import orinnetwork.jpstudy.application.comment.dto.CommentRequest;
import orinnetwork.jpstudy.application.comment.dto.CommentResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;

@Tag(name = "Comment API", description = "게시글 댓글 생성, 조회 및 삭제 관리")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 생성", description = "특정 게시글에 새로운 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "댓글을 작성할 게시글 ID", in = ParameterIn.PATH)
            @PathVariable Long postId,

            @Valid @RequestBody CommentRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest
    ) {
        Long memberId = userDetails.getMemberId();
        String ipAddress = IpUtil.getClientIp(httpServletRequest);

        CommentResponse response = commentService.createComment(postId, request, memberId, ipAddress);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<CommentResponse>> getComments(
            @Parameter(description = "댓글을 조회할 게시글 ID", in = ParameterIn.PATH)
            @PathVariable Long postId,

            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<CommentResponse> page = commentService.getComments(postId, pageable);
        return ResponseEntity.ok(page);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다. 댓글 작성자만 삭제 가능합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글이 속한 게시글 ID (이후 비정규화 게시글 댓글 개수 카우팅 줄여야 함)", in = ParameterIn.PATH)
            @PathVariable Long postId,

            @Parameter(description = "삭제할 댓글 ID", in = ParameterIn.PATH)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        commentService.deleteComment(commentId, memberId);

        return ResponseEntity.noContent().build();
    }
}