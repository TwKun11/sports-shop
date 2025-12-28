-- ============================================================================
-- Refresh Tokens Table - Optimized Schema
-- ============================================================================
-- Purpose: Stores refresh tokens for JWT authentication with rotation support
-- Security: Implements token rotation, family tracking, and revocation
-- ============================================================================

CREATE TABLE IF NOT EXISTS refresh_tokens (
    -- Primary Key
    id BIGSERIAL PRIMARY KEY,
    
    -- Foreign Key to Users
    user_id BIGINT NOT NULL,
    
    -- Token Identifiers
    jti VARCHAR(64) NOT NULL UNIQUE,
    refresh_token TEXT, -- Optional: store full token if needed (usually not recommended)
    token_family VARCHAR(64), -- For token rotation: tracks related tokens
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP, -- Track last usage for cleanup prioritization
    revoked_at TIMESTAMP, -- When token was revoked
    
    -- Status & Rotation
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    replaced_by VARCHAR(64), -- JTI of token that replaced this one (for rotation tracking)
    
    -- Security Context
    ip_address VARCHAR(45), -- IPv4 (15) or IPv6 (45 chars)
    user_agent VARCHAR(500), -- Browser/client information
    device_info VARCHAR(255), -- Additional device metadata
    
    -- Foreign Key Constraint
    CONSTRAINT fk_refresh_token_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    -- Data Integrity Constraints
    CONSTRAINT chk_expires_after_created 
        CHECK (expires_at > created_at),
    CONSTRAINT chk_revoked_at_valid 
        CHECK (revoked_at IS NULL OR revoked_at >= created_at),
    CONSTRAINT chk_last_used_valid 
        CHECK (last_used_at IS NULL OR last_used_at >= created_at),
    CONSTRAINT chk_revoked_consistency 
        CHECK ((revoked = FALSE AND revoked_at IS NULL) OR (revoked = TRUE))
);

-- ============================================================================
-- Indexes for Performance Optimization
-- ============================================================================

-- Index for finding active tokens by user (most common query)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active 
    ON refresh_tokens(user_id, revoked) 
    WHERE revoked = FALSE;

-- Index for finding token by JTI (unique lookup)
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_tokens_jti 
    ON refresh_tokens(jti);

-- Index for cleanup expired tokens (scheduled job)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at 
    ON refresh_tokens(expires_at) 
    WHERE revoked = FALSE;

-- Index for token family operations (rotation security)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_family 
    ON refresh_tokens(token_family) 
    WHERE token_family IS NOT NULL;

-- Index for finding replaced tokens (rotation tracking)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_replaced_by 
    ON refresh_tokens(replaced_by) 
    WHERE replaced_by IS NOT NULL;

-- Composite index for user token cleanup (by last_used_at)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_last_used 
    ON refresh_tokens(user_id, last_used_at NULLS LAST, created_at) 
    WHERE revoked = FALSE;

-- ============================================================================
-- Comments for Documentation
-- ============================================================================

COMMENT ON TABLE refresh_tokens IS 
    'Stores refresh tokens for JWT authentication with support for token rotation, family tracking, and security monitoring';

COMMENT ON COLUMN refresh_tokens.id IS 
    'Primary key - auto-incrementing identifier';

COMMENT ON COLUMN refresh_tokens.user_id IS 
    'Foreign key to users table - identifies token owner';

COMMENT ON COLUMN refresh_tokens.jti IS 
    'JWT ID - unique identifier for the token (UUID format recommended)';

COMMENT ON COLUMN refresh_tokens.refresh_token IS 
    'Optional: full refresh token value (usually not stored for security)';

COMMENT ON COLUMN refresh_tokens.token_family IS 
    'Token family identifier for rotation - all tokens in a family are revoked together if reuse detected';

COMMENT ON COLUMN refresh_tokens.created_at IS 
    'Timestamp when token was created';

COMMENT ON COLUMN refresh_tokens.expires_at IS 
    'Timestamp when token expires - must be after created_at';

COMMENT ON COLUMN refresh_tokens.last_used_at IS 
    'Timestamp of last token usage - used for cleanup prioritization';

COMMENT ON COLUMN refresh_tokens.revoked_at IS 
    'Timestamp when token was revoked - NULL if not revoked';

COMMENT ON COLUMN refresh_tokens.revoked IS 
    'Boolean flag indicating if token is revoked';

COMMENT ON COLUMN refresh_tokens.replaced_by IS 
    'JTI of the token that replaced this one during rotation';

COMMENT ON COLUMN refresh_tokens.ip_address IS 
    'IP address of the client that created/used the token (IPv4 or IPv6)';

COMMENT ON COLUMN refresh_tokens.user_agent IS 
    'User agent string from HTTP request header';

COMMENT ON COLUMN refresh_tokens.device_info IS 
    'Additional device metadata (OS, device type, etc.)';

-- ============================================================================
-- Performance Optimization Notes
-- ============================================================================
-- 1. Partial indexes (WHERE clauses) reduce index size and improve performance
-- 2. Composite indexes support common query patterns
-- 3. NULLS LAST in index helps with cleanup queries
-- 4. CASCADE delete ensures tokens are removed when user is deleted
-- 5. Check constraints ensure data integrity at database level
-- ============================================================================



