package orinnetwork.jpstudy.application.image;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlRequest;
import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlResponse;
import orinnetwork.jpstudy.application.image.port.out.StoragePort;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final StoragePort storagePort;

    public GeneratePresignedUrlResponse generatePresignedUrl(GeneratePresignedUrlRequest request) {
        String uniqueFileName = createUniqueFileName(request.fileName());

        GeneratePresignedUrlResponse response = storagePort.generatePresignedUrl(uniqueFileName);
        return new GeneratePresignedUrlResponse(response.presignedUrl(), uniqueFileName);
    }

    private String createUniqueFileName(String fileName) {
        return UUID.randomUUID().toString() + "-" + fileName;
    }
}
