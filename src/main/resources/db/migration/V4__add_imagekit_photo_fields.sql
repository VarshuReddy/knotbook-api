ALTER TABLE project_photos
    ADD COLUMN imagekit_file_is VARCHAR(100) NOT NULL,
    ADD COLUMN file_path VARCHAR(500) NOT NULL;