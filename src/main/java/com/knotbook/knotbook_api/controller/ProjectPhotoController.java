package com.knotbook.knotbook_api.controller;

import com.knotbook.knotbook_api.dto.response.ProjectPhotoResponse;
import com.knotbook.knotbook_api.service.ProjectPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/photos")
@RequiredArgsConstructor
public class ProjectPhotoController {
    private final ProjectPhotoService projectPhotoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectPhotoResponse> uploadPhoto(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) Boolean isCover,
            @RequestParam(required = false) LocalDateTime takenAt,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        ProjectPhotoResponse response =
                projectPhotoService.uploadPhoto(
                        userId,
                        projectId,
                        file,
                        caption,
                        isCover,
                        takenAt
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectPhotoResponse>> getPhotos(
            @PathVariable Long projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        List<ProjectPhotoResponse> response =
                projectPhotoService.getPhotos(userId, projectId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long projectId,
            @PathVariable Long photoId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        projectPhotoService.deletePhoto(userId, projectId, photoId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{photoId}/cover")
    public ResponseEntity<Void> updateCoverPhoto(
            @PathVariable Long projectId,
            @PathVariable Long photoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        projectPhotoService.updateCoverPhoto(userId, projectId, photoId);

        return ResponseEntity.noContent().build();
    }
}
