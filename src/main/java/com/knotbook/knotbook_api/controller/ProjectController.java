package com.knotbook.knotbook_api.controller;


import com.knotbook.knotbook_api.dto.request.AddProjectTimeRequest;
import com.knotbook.knotbook_api.dto.request.CreateProjectRequest;
import com.knotbook.knotbook_api.dto.response.ProjectDetailResponse;
import com.knotbook.knotbook_api.dto.response.ProjectListResponse;
import com.knotbook.knotbook_api.dto.response.ProjectResponse;
import com.knotbook.knotbook_api.enity.ProjectStatus;
import com.knotbook.knotbook_api.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request , @AuthenticationPrincipal Jwt jwt){
        Long userId = Long.parseLong(jwt.getSubject());

        ProjectResponse response =
                projectService.createProject( userId,request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ProjectListResponse> getProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        ProjectListResponse response = projectService.getProjects(
                userId,
                status,
                search,
                page,
                size
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetails(
            @PathVariable Long projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        ProjectDetailResponse response =
                projectService.getProjectDetails(userId, projectId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return ResponseEntity.ok(
                projectService.updateProject(userId, projectId, request)
        );
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        projectService.deleteProject(userId, projectId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/time")
    public ResponseEntity<ProjectDetailResponse> addProjectTime(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectTimeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return ResponseEntity.ok(
                projectService.addProjectTime(userId, projectId, request)
        );
    }
}
