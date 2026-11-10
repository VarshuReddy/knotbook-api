package com.knotbook.knotbook_api.repository;

import com.knotbook.knotbook_api.enity.Project;
import com.knotbook.knotbook_api.enity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p FROM Project p
        WHERE p.user.id = :userId
        AND p.isDeleted = false
        AND (:status IS NULL OR p.status = :status)
        AND (
            :search IS NULL
            OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """)
    Page<Project> findUserProjects(
            @Param("userId") Long userId,
            @Param("status") ProjectStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    long countByUserIdAndIsDeletedFalse(Long userId);

    long countByUserIdAndStatusAndIsDeletedFalse(
            Long userId,
            ProjectStatus status
    );

    @Query("""
    SELECT COALESCE(SUM(p.totalTimeMinutes), 0)
    FROM Project p
    WHERE p.user.id = :userId
    AND p.isDeleted = false
""")
    Long getTotalTimeByUserId(@Param("userId") Long userId);


    Page<Project> findByUserIdAndIsDeletedFalse(
            Long userId,
            Pageable pageable
    );
}
