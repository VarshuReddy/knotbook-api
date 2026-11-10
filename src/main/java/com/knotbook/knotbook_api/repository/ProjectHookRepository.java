package com.knotbook.knotbook_api.repository;

import com.knotbook.knotbook_api.enity.ProjectHook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectHookRepository
        extends JpaRepository<ProjectHook, Long> {
    List<ProjectHook> findAllByProjectId(Long projectId);
    void deleteAllByProjectId(Long projectId);
}
