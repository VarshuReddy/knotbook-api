package com.knotbook.knotbook_api.service;


import com.knotbook.knotbook_api.dto.request.SaveProjectNoteRequest;
import com.knotbook.knotbook_api.dto.response.ProjectNoteResponse;
import com.knotbook.knotbook_api.enity.Project;
import com.knotbook.knotbook_api.enity.ProjectNote;
import com.knotbook.knotbook_api.repository.ProjectNoteRepository;
import com.knotbook.knotbook_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProjectNoteService {

    private final ProjectNoteRepository noteRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectNoteResponse saveNotes(
            Long userId,
            Long projectId,
            SaveProjectNoteRequest request
    ) {

        Project project = getOwnedProject(userId, projectId);

        ProjectNote note = noteRepository.findByProjectId(projectId)
                .orElseGet(() -> new ProjectNote(
                        project,
                        request.getContent()
                ));

        // Preserve the content exactly as received
        note.updateContent(request.getContent());

        ProjectNote savedNote = noteRepository.save(note);

        return toResponse(savedNote);
    }

    // Retrieve notes
    @Transactional(readOnly = true)
    public ProjectNoteResponse getNotes(
            Long userId,
            Long projectId
    ) {

        getOwnedProject(userId, projectId);

        return noteRepository.findByProjectId(projectId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notes not found"
                ));
    }

    // Delete notes
    @Transactional
    public void deleteNotes(
            Long userId,
            Long projectId
    ) {

        getOwnedProject(userId, projectId);

        ProjectNote note = noteRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notes not found"
                ));

        noteRepository.delete(note);
    }

    // Ensure the project belongs to the logged-in user
    private Project getOwnedProject(
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

        return project;
    }

    private ProjectNoteResponse toResponse(ProjectNote note) {

        return new ProjectNoteResponse(
                note.getId(),
                note.getProject().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
