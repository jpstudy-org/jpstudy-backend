package orinnetwork.jpstudy.presentation.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.image.ImageService;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlRequest;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlResponse;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned-url")
    public ResponseEntity<GeneratePresignedUrlResponse> generatePresignedUrl(
            @RequestBody GeneratePresignedUrlRequest request
    ) {
        GeneratePresignedUrlResponse response = imageService.generatePresignedUrl(request);
        return ResponseEntity.ok(response);
    }
}