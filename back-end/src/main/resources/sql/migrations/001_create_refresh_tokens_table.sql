-- ============================================================================
-- Migration: Create refresh_tokens table
-- Version: 001
-- Date: 2024
-- Description: Creates optimized refresh_tokens table with indexes and constraints
-- ============================================================================

BEGIN;

-- Drop table if exists (for development/testing only - remove in production)
-- DROP TABLE IF EXISTS refresh_tokens CASCADE;

-- Create table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    jti VARCHAR(64) NOT NULL UNIQUE,
    refresh_token TEXT,
    token_family VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP,
    revoked_at TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    replaced_by VARCHAR(64),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    device_info VARCHAR(255),
    CONSTRAINT fk_refresh_token_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    CONSTRAINT chk_expires_after_created 
        CHECK (expires_at > created_at),
    CONSTRAINT chk_revoked_at_valid 
        CHECK (revoked_at IS NULL OR revoked_at >= created_at),
    CONSTRAINT chk_last_used_valid 
        CHECK (last_used_at IS NULL OR last_used_at >= created_at),
    CONSTRAINT chk_revoked_consistency 
        CHECK ((revoked = FALSE AND revoked_at IS NULL) OR (revoked = TRUE))
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active 
    ON refresh_tokens(user_id, revoked) 
    WHERE revoked = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_tokens_jti 
    ON refresh_tokens(jti);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at 
    ON refresh_tokens(expires_at) 
    WHERE revoked = FALSE;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_family 
    ON refresh_tokens(token_family) 
    WHERE token_family IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_replaced_by 
    ON refresh_tokens(replaced_by) 
    WHERE replaced_by IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_last_used 
    ON refresh_tokens(user_id, last_used_at NULLS LAST, created_at) 
    WHERE revoked = FALSE;

-- Add comments
COMMENT ON TABLE refresh_tokens IS 
    'Stores refresh tokens for JWT authentication with support for token rotation, family tracking, and security monitoring';

COMMENT ON COLUMN refresh_tokens.jti IS 
    'JWT ID - unique identifier for the token (UUID format recommended)';

COMMENT ON COLUMN refresh_tokens.token_family IS 
    'Token family identifier for rotation - all tokens in a family are revoked together if reuse detected';

COMMENT ON COLUMN refresh_tokens.replaced_by IS 
    'JTI of the token that replaced this one during rotation';

COMMIT;



