package orinnetwork.jpstudy.presentation.comment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest
    ) {
        Long memberId = userDetails.getMemberId();
        String ipAddress = IpUtil.getClientIp(httpServletRequest);

        CommentResponse response = commentService.createComment(postId, request, memberId, ipAddress);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CustomPageResponse<CommentResponse>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<CommentResponse> page = commentService.getComments(postId, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        commentService.deleteComment(commentId, memberId);

        return ResponseEntity.noContent().build();
    }
}