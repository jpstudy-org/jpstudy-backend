package orinnetwork.jpstudy.presentation.admin.post;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.post.AdminPostService;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final AdminPostService adminPostService;

    @PostMapping("/notices")
    public ResponseEntity<PostDetailResponse> createNotice(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
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

    @PutMapping("/notices/{postId}")
    public ResponseEntity<PostDetailResponse> updateNotice(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request
    ) {
        PostDetailResponse response = adminPostService.updateNotice(postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
