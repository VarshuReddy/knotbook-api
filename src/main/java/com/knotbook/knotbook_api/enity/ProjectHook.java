package com.knotbook.knotbook_api.enity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "project_hooks")
@Getter
public class ProjectHook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "hook_size_mm", nullable = false, precision = 4, scale = 1)
    private BigDecimal hookSizeMm;

    protected ProjectHook() {
    }

    public ProjectHook(Project project, BigDecimal hookSizeMm) {
        this.project = project;
        this.hookSizeMm = hookSizeMm;
    }
}