package com.knotbook.knotbook_api.service;


import com.knotbook.knotbook_api.dto.request.AddProjectTimeRequest;
import com.knotbook.knotbook_api.dto.request.CreateProjectRequest;
import com.knotbook.knotbook_api.dto.response.*;
import com.knotbook.knotbook_api.enity.*;
import com.knotbook.knotbook_api.exception.ApiException;
import com.knotbook.knotbook_api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectYarnRepository yarnRepository;
    private final ProjectHookRepository hookRepository;
    private final UserRepository userRepository;
    private final ProjectPhotoRepository photoRepository;
    private final ProjectNoteRepository noteRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectYarnRepository yarnRepository,
                          ProjectHookRepository hookRepository, UserRepository userRepository,
                          ProjectPhotoRepository photoRepository, ProjectNoteRepository noteRepository) {
        this.projectRepository = projectRepository;
        this.yarnRepository = yarnRepository;
        this.hookRepository = hookRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.noteRepository = noteRepository;
    }



    @Transactional
    public ProjectResponse createProject(
            Long userId,
            CreateProjectRequest request
    ) {

        // 1. Find the authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create and save the project
        Project project = new Project(
                user,
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );

        Project savedProject = projectRepository.save(project);

        // 3. Save yarn details, if provided
        List<YarnResponse> yarnResponses = List.of();

        if (request.getYarns() != null && !request.getYarns().isEmpty()) {

            List<ProjectYarn> yarns = request.getYarns()
                    .stream()
                    .map(yarn -> new ProjectYarn(
                            savedProject,
                            yarn.getColor(),
                            yarn.getWeight(),
                            yarn.getType()
                    ))
                    .toList();

            List<ProjectYarn> savedYarns = yarnRepository.saveAll(yarns);

            yarnResponses = savedYarns.stream()
                    .map(yarn -> new YarnResponse(
                            yarn.getId(),
                            yarn.getColor(),
                            yarn.getWeight(),
                            yarn.getType()
                    ))
                    .toList();
        }

        // 4. Save hook details, if provided
        List<HookResponse> hookResponses = List.of();

        if (request.getHooks() != null && !request.getHooks().isEmpty()) {

            List<ProjectHook> hooks = request.getHooks().stream()
                    .map(hook-> new ProjectHook(
                            savedProject,
                            hook.getHookSizeMm())).toList();

            List<ProjectHook> savedHooks = hookRepository.saveAll(hooks);

            hookResponses = savedHooks.stream().map(
                    hook-> new HookResponse(hook.getId(),hook.getHookSizeMm())
            ).toList();
        }

        // 5. Build and return response
        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getTitle(),
                savedProject.getDescription(),
                savedProject.getCategory(),
                savedProject.getStatus(),
                savedProject.getStartDate(),
                savedProject.getEndDate(),
                savedProject.getTotalTimeMinutes(),
                yarnResponses,
                hookResponses,
                savedProject.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ProjectListResponse getProjects(
            Long userId,
            ProjectStatus status,
            String search,
            int page,
            int size
    ) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 50"
            );
        }

        String searchTerm = (search == null || search.isBlank())
                ? null
                : search;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<Project> projects = projectRepository.findUserProjects(
                userId,
                status,
                searchTerm,
                pageable
        );

        List<Long> projectIds = projects.getContent()
                .stream()
                .map(Project::getId)
                .toList();

        // Retrieve all cover photos in one query
        Map<Long, ProjectPhoto> coverPhotos = projectIds.isEmpty()
                ? Collections.emptyMap()
                : photoRepository
                .findAllByProjectIdInAndIsCoverTrue(projectIds)
                .stream()
                .collect(Collectors.toMap(
                        photo -> photo.getProject().getId(),
                        Function.identity()
                ));

        List<ProjectListItemResponse> content =
                projects.getContent().stream()
                        .map(project -> {

                            ProjectPhoto cover = coverPhotos.get(project.getId());

                            return new ProjectListItemResponse(
                                    project.getId(),
                                    project.getTitle(),
                                    project.getCategory(),
                                    project.getStatus(),
                                    cover != null ? cover.getImageUrl() : null,
                                    project.getStartDate(),
                                    project.getTotalTimeMinutes()
                            );
                        })
                        .toList();

        return new ProjectListResponse(
                content,
                projects.getNumber(),
                projects.getSize(),
                projects.getTotalElements(),
                projects.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetails(
            Long userId,
            Long projectId
    ) {

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

        List<YarnResponse> yarns = yarnRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(yarn -> new YarnResponse(
                        yarn.getId(),
                        yarn.getColor(),
                        yarn.getWeight(),
                        yarn.getType()
                ))
                .toList();

        List<HookResponse> hooks = hookRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(hook -> new HookResponse(
                        hook.getId(),
                        hook.getHookSizeMm()
                ))
                .toList();

        List<ProjectPhotoResponse> photos = photoRepository
                .findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(photo -> new ProjectPhotoResponse(
                        photo.getId(),
                        projectId,
                        photo.getImageUrl(),
                        photo.getCaption(),
                        photo.isCover(),
                        photo.getTakenAt(),
                        photo.getCreatedAt()
                ))
                .toList();

        ProjectNoteResponse notes = noteRepository
                .findByProjectId(projectId)
                .map(note -> new ProjectNoteResponse(
                        note.getId(),
                        projectId,
                        note.getContent(),
                        note.getCreatedAt(),
                        note.getUpdatedAt()
                ))
                .orElse(null);

        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getCategory(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate(),
                project.getTotalTimeMinutes(),
                yarns,
                hooks,
                photos,
                notes,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    @Transactional
    public ProjectDetailResponse updateProject(
            Long userId,
            Long projectId,
            CreateProjectRequest request
    ){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(userId) || project.isDeleted()) {
            throw new RuntimeException("Project not found");
        }

        project.updateDetails(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );
        projectRepository.save(project);

        yarnRepository.deleteAllByProjectId(projectId);
        List<ProjectYarn> yarnList = request.getYarns().stream()
                .map(yarnRequest -> new ProjectYarn(project,yarnRequest.getColor(),
                        yarnRequest.getWeight(),yarnRequest.getType())).toList();
        yarnRepository.saveAll(yarnList);
        hookRepository.deleteAllByProjectId(projectId);

        List<ProjectHook> hooks = request.getHooks().stream()
                .map(hook -> new ProjectHook(
                        project,
                        hook.getHookSizeMm()
                ))
                .toList();

        hookRepository.saveAll(hooks);

        // 5. Return updated project
        return getProjectDetails(userId, projectId);

    }

    @Transactional
    public void deleteProject(Long userId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(userId) || project.isDeleted()) {
            throw new RuntimeException("Project not found");
        }

        project.softDelete();
    }

    @Transactional
    public ProjectDetailResponse addProjectTime(
            Long userId,
            Long projectId,
            AddProjectTimeRequest request
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(userId) || project.isDeleted()) {
            throw new RuntimeException("Project not found");
        }

        project.addTime(request.getAdditionalMinutes());

        return getProjectDetails(userId, projectId);
    }

    @Transactional(readOnly = true)
    public List<ProjectListItemResponse> getRecentProjects(
            Long userId,
            int limit
    ) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<Project> projectPage =
                projectRepository.findByUserIdAndIsDeletedFalse(
                        userId,
                        pageable
                );

        List<Project> projects = projectPage.getContent();

        // Fetch cover photos for all projects in one query
        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        Map<Long, String> coverPhotos = photoRepository
                .findAllByProjectIdInAndIsCoverTrue(projectIds)
                .stream()
                .collect(Collectors.toMap(
                        photo -> photo.getProject().getId(),
                        ProjectPhoto::getImageUrl
                ));

        return projects.stream()
                .map(project -> new ProjectListItemResponse(
                        project.getId(),
                        project.getTitle(),
                        project.getCategory(),
                        project.getStatus(),
                        coverPhotos.get(project.getId()),
                        project.getStartDate(),
                        project.getTotalTimeMinutes()
                ))
                .toList();
    }
}

