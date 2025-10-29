package orinnetwork.jpstudy.presentation.post;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import orinnetwork.jpstudy.application.post.PostService;
import orinnetwork.jpstudy.application.post.dto.PostRequest;
import orinnetwork.jpstudy.application.post.dto.PostDetailResponse;
import orinnetwork.jpstudy.application.post.dto.PostSummaryResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(
            @Valid @RequestBody PostRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails,
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

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        PostDetailResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        postService.deletePost(id, memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<PostSummaryResponse> responsePage = postService.getPosts(pageable);
        return ResponseEntity.ok(responsePage);
    }
}