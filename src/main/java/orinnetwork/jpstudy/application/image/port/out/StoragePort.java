package orinnetwork.jpstudy.application.image.port.out;

import orinnetwork.jpstudy.application.image.dto.GeneratePresignedUrlResponse;

public interface StoragePort {
    GeneratePresignedUrlResponse generatePresignedUrl(String fileName, long fileSize, String contentType);
}
