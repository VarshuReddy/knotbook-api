package com.knotbook.knotbook_api.repository;

import com.knotbook.knotbook_api.enity.ProjectNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectNoteRepository   extends JpaRepository<ProjectNote, Long> {

    Optional<ProjectNote> findByProjectId(Long projectId);}
