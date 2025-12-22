package orinnetwork.jpstudy.presentation.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.image.ImageService;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlRequest;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlResponse;

@Tag(name = "Image API", description = "이미지 업로드 및 관리 (Pre-signed URL 생성)")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "Pre-signed URL 생성",
            description = "클라이언트가 AWS S3와 같은 클라우드 스토리지에 직접 파일을 업로드할 수 있도록 임시 서명된(Pre-signed) URL과 파일 경로를 발급합니다."
    )
    @PostMapping("/presigned-url")
    public ResponseEntity<GeneratePresignedUrlResponse> generatePresignedUrl(
            @RequestBody GeneratePresignedUrlRequest request
    ) {
        GeneratePresignedUrlResponse response = imageService.generatePresignedUrl(request);
        return ResponseEntity.ok(response);
    }
}