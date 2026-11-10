ALTER TABLE project_notes
    DROP COLUMN title;

ALTER TABLE project_notes
    ADD CONSTRAINT uk_project_notes_project_id
        UNIQUE (project_id);