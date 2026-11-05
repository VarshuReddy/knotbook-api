CREATE TABLE  app_users(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL ,
    email VARCHAR(255) NOT NULL ,
    password_hash VARCHAR(255) NOT NULL ,
    created_at TIMESTAMP(6) NOT NULL ,
    updated_at TIMESTAMP(6) NOT NULL ,
    PRIMARY KEY (id) ,
    CONSTRAINT  uk_app_users_email UNIQUE (email)
)