CREATE TABLE projects (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,

                          title VARCHAR(150) NOT NULL,
                          description TEXT NULL,
                          category VARCHAR(50) NULL,

                          status VARCHAR(30) NOT NULL DEFAULT 'PLANNING',

                          start_date DATE NULL,
                          end_date DATE NULL,

                          total_time_minutes BIGINT NOT NULL DEFAULT 0,

                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          deleted_at TIMESTAMP(6) NULL,

                          created_at TIMESTAMP(6) NOT NULL,
                          updated_at TIMESTAMP(6) NOT NULL,

                          PRIMARY KEY (id),

                          CONSTRAINT fk_projects_user
                              FOREIGN KEY (user_id)
                                  REFERENCES app_users(id)
                                  ON DELETE CASCADE,

                          INDEX idx_projects_user_id (user_id),
                          INDEX idx_projects_user_status (user_id, status),
                          INDEX idx_projects_user_deleted (user_id, is_deleted)
);


CREATE TABLE project_yarns (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,

    color VARCHAR(80) NULL,
    weight VARCHAR(50) NULL,
    type VARCHAR(50) NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_project_yarns_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    INDEX idx_project_yarns_project_id (project_id)
);


CREATE TABLE project_hooks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,

    hook_size_mm DECIMAL(4,2) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_project_hooks_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    INDEX idx_project_hooks_project_id (project_id)
);



CREATE TABLE project_photos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,

    image_url VARCHAR(1000) NOT NULL,
    caption VARCHAR(500) NULL,
    is_cover BOOLEAN NOT NULL DEFAULT FALSE,

    taken_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_project_photos_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    INDEX idx_project_photos_project_id (project_id)
);



CREATE TABLE project_notes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,

    title VARCHAR(150) NULL,
    content TEXT NOT NULL,

    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_project_notes_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    INDEX idx_project_notes_project_id (project_id)
);


