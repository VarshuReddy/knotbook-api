package com.knotbook.knotbook_api.enity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_notes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_notes_project_id",
                        columnNames = "project_id"
                )
        }
)
@Getter
public class ProjectNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected ProjectNote() {
    }

    public ProjectNote(Project project, String content) {
        this.project = project;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
