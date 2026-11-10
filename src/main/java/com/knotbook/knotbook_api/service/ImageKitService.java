package com.knotbook.knotbook_api.service;


import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileDeleteParams;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageKitService {

    private final ImageKitClient imageKitClient;

    public ImageKitUploadResult uploadImage(
            byte[] imageBytes,
            String fileName,
            String folder
    ) {

        FileUploadParams params = FileUploadParams.builder()
                .file(imageBytes)
                .fileName(fileName)
                .folder(folder)
                .build();

        FileUploadResponse response =
                imageKitClient.files().upload(params);

        return new ImageKitUploadResult(
                response.fileId(),
                response.url(),
                response.filePath()
        );
    }

    public void deleteImage(String fileId) {

        FileDeleteParams params = FileDeleteParams.builder()
                .fileId(fileId)
                .build();

        imageKitClient.files().delete(params);
    }
}
