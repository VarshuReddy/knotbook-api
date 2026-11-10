package com.knotbook.knotbook_api.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "project_photos")
@Getter
@NoArgsConstructor
public class ProjectPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "imagekit_file_id", nullable = false, length = 100)
    private String imagekitFileId;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Column(length = 255)
    private String caption;

    @Column(name = "is_cover", nullable = false)
    private boolean isCover = false;

    private LocalDateTime takenAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ProjectPhoto(Project project, Optional<String> s, Optional<String> url, Optional<String> string, String caption, boolean cover, LocalDateTime takenAt) {
    }

    public ProjectPhoto(
            Project project,
            String imagekitFileId,
            String imageUrl,
            String filePath,
            String caption,
            boolean isCover,
            LocalDateTime takenAt
    ) {
        this.project = project;
        this.imagekitFileId = imagekitFileId;
        this.imageUrl = imageUrl;
        this.filePath = filePath;
        this.caption = caption;
        this.isCover = isCover;
        this.takenAt = takenAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public void updateCover(boolean isCover) {
        this.isCover = isCover;
    }
}