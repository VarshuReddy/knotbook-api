package com.knotbook.knotbook_api.repository;

import com.knotbook.knotbook_api.enity.ProjectYarn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectYarnRepository extends JpaRepository<ProjectYarn, Long> {
    List<ProjectYarn> findAllByProjectId(Long projectId);
    void deleteAllByProjectId(Long projectId);
}
