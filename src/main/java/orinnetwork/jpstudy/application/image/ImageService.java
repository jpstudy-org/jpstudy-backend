package orinnetwork.jpstudy.application.image;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlRequest;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlResponse;
import orinnetwork.jpstudy.application.image.port.out.StoragePort;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final StoragePort storagePort;
    private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "application/pdf"
    );

    public GeneratePresignedUrlResponse generatePresignedUrl(GeneratePresignedUrlRequest request) {

        if (request.fileSize() > MAX_FILE_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        if (request.contentType() == null || !ALLOWED_CONTENT_TYPES.contains(request.contentType())) {
            throw new CustomException(ErrorCode.FILE_TYPE_NOT_SUPPORTED, request.contentType());
        }

        String uniqueFileName = createUniqueFileName(request.fileName());

        GeneratePresignedUrlResponse response = storagePort.generatePresignedUrl(
                uniqueFileName,
                request.fileSize(),
                request.contentType()
        );

        return new GeneratePresignedUrlResponse(response.presignedUrl(), uniqueFileName);
    }

    private String createUniqueFileName(String fileName) {
        return UUID.randomUUID() + "-" + fileName;
    }
}
