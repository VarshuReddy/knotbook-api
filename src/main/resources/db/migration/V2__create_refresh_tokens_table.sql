CREATE TABLE refresh_tokens(
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL ,
    token_hash VARCHAR(64) NOT NULL ,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_refresh_token_hash UNIQUE (token_hash),

    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE  CASCADE ,

    INDEX idx_refresh_token_user (user_id)
)