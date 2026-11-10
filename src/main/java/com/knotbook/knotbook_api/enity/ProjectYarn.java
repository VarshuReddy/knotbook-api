package com.knotbook.knotbook_api.enity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "project_yarns")
@Getter
public class ProjectYarn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(length = 100)
    private String color;

    @Column(length = 50)
    private String weight;

    @Column(length = 100)
    private String type;

    protected ProjectYarn() {
    }

    public ProjectYarn(Project project, String color,
                       String weight, String type) {
        this.project = project;
        this.color = color;
        this.weight = weight;
        this.type = type;
    }
}