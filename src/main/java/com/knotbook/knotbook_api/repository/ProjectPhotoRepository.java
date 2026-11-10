package com.knotbook.knotbook_api.repository;

import com.knotbook.knotbook_api.enity.ProjectPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectPhotoRepository  extends JpaRepository<ProjectPhoto,Long> {


    List<ProjectPhoto> findAllByProjectIdOrderByCreatedAtDesc(Long projectId);

    Optional<ProjectPhoto> findByIdAndProjectId(Long id, Long projectId);

    List<ProjectPhoto> findAllByProjectIdInAndIsCoverTrue(
            List<Long> projectIds
    );

    @Modifying
    @Query("""
    UPDATE ProjectPhoto p
    SET p.isCover = false
    WHERE p.project.id = :projectId
""")
    void clearCoverPhotos(@Param("projectId") Long projectId);
}
