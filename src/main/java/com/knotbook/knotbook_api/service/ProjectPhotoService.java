package com.knotbook.knotbook_api.service;



import com.knotbook.knotbook_api.dto.response.ProjectPhotoResponse;
import com.knotbook.knotbook_api.enity.Project;
import com.knotbook.knotbook_api.enity.ProjectPhoto;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.ProjectPhotoRepository;
import com.knotbook.knotbook_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectPhotoService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final ProjectRepository projectRepository;
    private final ProjectPhotoRepository photoRepository;
    private final ImageKitService imageKitService;

    @Transactional
    public ProjectPhotoResponse uploadPhoto(
            Long userId,
            Long projectId,
            MultipartFile file,
            String caption,
            Boolean isCover,
            LocalDateTime takenAt
    ) {

        // 1. Verify project ownership
        Project project = getOwnedProject(userId, projectId);

        // 2. Validate the uploaded file
        validateImage(file);

        // 3. Generate a unique filename
        String originalFilename = file.getOriginalFilename();

        String extension = getExtension(originalFilename);

        String fileName = UUID.randomUUID() + extension;

        // 4. Upload image to ImageKit
        ImageKitUploadResult uploadResult;

        try {
            uploadResult = imageKitService.uploadImage(
                    file.getBytes(),
                    fileName,
                    "/knotbook/projects/" + projectId
            );
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to read uploaded image"
            );
        }

        // 5. Save metadata in MySQL
        try {

            boolean cover = Boolean.TRUE.equals(isCover);

            if (cover) {
                photoRepository
                        .findAllByProjectIdOrderByCreatedAtDesc(projectId)
                        .forEach(photo -> photo.updateCover(false));
            }

            ProjectPhoto photo = new ProjectPhoto(
                    project,
                    uploadResult.fileId().orElseThrow(
                            () -> new IllegalStateException("ImageKit did not return an image ID")
                    ),
                    uploadResult.url().orElseThrow(
                            () -> new IllegalStateException("ImageKit did not return an image URL")
                    ),
                    uploadResult.filePath().orElseThrow(
                            () -> new IllegalStateException("ImageKit did not return a file path")
                    ),
                    caption,
                    cover,
                    takenAt
            );

            ProjectPhoto savedPhoto = photoRepository.save(photo);

            return toResponse(savedPhoto);

        } catch (RuntimeException exception) {

            // Database failed after ImageKit upload.
            // Attempt to remove the uploaded file to avoid an orphan.
            try {
                imageKitService.deleteImage(uploadResult.fileId().orElseThrow(
                        () -> new IllegalStateException("Invalid ID")));
            } catch (Exception cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ProjectPhotoResponse> getPhotos(
            Long userId,
            Long projectId
    ) {

        getOwnedProject(userId, projectId);

        return photoRepository
                .findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deletePhoto(
            Long userId,
            Long projectId,
            Long photoId
    ) {

        getOwnedProject(userId, projectId);

        ProjectPhoto photo = photoRepository
                .findByIdAndProjectId(photoId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Photo not found"
                ));

        // Delete cloud file first
        imageKitService.deleteImage(photo.getImagekitFileId());

        // Then remove database record
        photoRepository.delete(photo);
    }

    private Project getOwnedProject(Long userId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        if (!project.getUser().getId().equals(userId)
                || project.isDeleted()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Project not found"
            );
        }

        return project;
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select an image"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image size must not exceed 10 MB"
            );
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPEG, PNG and WEBP images are allowed"
            );
        }
    }

    private String getExtension(String filename) {

        if (filename == null || !filename.contains(".")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid image filename"
            );
        }

        String extension = filename.substring(
                filename.lastIndexOf(".")
        ).toLowerCase();

        if (!List.of(".jpg", ".jpeg", ".png", ".webp")
                .contains(extension)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported image extension"
            );
        }

        return extension;
    }

    private ProjectPhotoResponse toResponse(ProjectPhoto photo) {

        return new ProjectPhotoResponse(
                photo.getId(),
                photo.getProject().getId(),
                photo.getImageUrl(),
                photo.getCaption(),
                photo.isCover(),
                photo.getTakenAt(),
                photo.getCreatedAt()
        );
    }

    @Transactional
    public void updateCoverPhoto(Long userId, Long projectId, Long photoId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(userId) || project.isDeleted()) {
            throw new RuntimeException("Project not found");
        }

        ProjectPhoto photo = photoRepository
                .findByIdAndProjectId(photoId, projectId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        photoRepository.clearCoverPhotos(projectId);

        photo.updateCover(true);
    }
}