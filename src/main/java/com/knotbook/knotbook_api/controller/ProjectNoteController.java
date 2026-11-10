package com.knotbook.knotbook_api.controller;



import com.knotbook.knotbook_api.dto.request.SaveProjectNoteRequest;
import com.knotbook.knotbook_api.dto.response.ProjectNoteResponse;
import com.knotbook.knotbook_api.service.ProjectNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/notes")
@RequiredArgsConstructor
public class ProjectNoteController {

    private final ProjectNoteService noteService;

    @PutMapping
    public ResponseEntity<ProjectNoteResponse> saveNotes(
            @PathVariable Long projectId,
            @Valid @RequestBody SaveProjectNoteRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        ProjectNoteResponse response =
                noteService.saveNotes(userId, projectId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ProjectNoteResponse> getNotes(
            @PathVariable Long projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        return ResponseEntity.ok(
                noteService.getNotes(userId, projectId)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotes(
            @PathVariable Long projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.parseLong(jwt.getSubject());

        noteService.deleteNotes(userId, projectId);

        return ResponseEntity.noContent().build();
    }
}