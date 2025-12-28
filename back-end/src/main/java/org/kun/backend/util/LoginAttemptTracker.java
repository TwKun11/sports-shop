package org.kun.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Theo dõi các nỗ cố đăng nhập thất bại cho từng user.
 * Được sử dụng để ngăn chặn brute force attacks.
 */
@Component
@Slf4j
public class LoginAttemptTracker {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    private final Map<String, LoginAttempt> loginAttempts = new HashMap<>();

    /**
     * Ghi nhận một lần đăng nhập thất bại
     */
    public void recordFailedAttempt(String usernameOrEmail) {
        String key = usernameOrEmail.toLowerCase();
        
        LoginAttempt attempt = loginAttempts.getOrDefault(key, new LoginAttempt());
        
        // Nếu tài khoản bị khóa, kiểm tra xem có vượt quá thời gian khóa không
        if (attempt.isLocked()) {
            LocalDateTime now = LocalDateTime.now();
            long minutesPassed = ChronoUnit.MINUTES.between(attempt.getLockedAt(), now);
            
            if (minutesPassed > LOCK_TIME_MINUTES) {
                // Thời gian khóa hết, reset attempts
                attempt = new LoginAttempt();
                log.info("Account lock expired for: {}", usernameOrEmail);
            } else {
                log.warn("Account is locked for: {} ({} minutes remaining)", 
                    usernameOrEmail, LOCK_TIME_MINUTES - minutesPassed);
                throw new RuntimeException("Account is temporarily locked. Try again later.");
            }
        }
        
        attempt.incrementAttempts();
        log.warn("Failed login attempt for: {} ({}/{})", 
            usernameOrEmail, attempt.getAttempts(), MAX_FAILED_ATTEMPTS);
        
        if (attempt.getAttempts() >= MAX_FAILED_ATTEMPTS) {
            attempt.lock();
            log.warn("Account locked due to too many failed attempts: {}", usernameOrEmail);
        }
        
        loginAttempts.put(key, attempt);
    }

    /**
     * Ghi nhận một lần đăng nhập thành công
     */
    public void recordSuccessfulAttempt(String usernameOrEmail) {
        String key = usernameOrEmail.toLowerCase();
        loginAttempts.remove(key);
        log.info("Login successful for: {}", usernameOrEmail);
    }

    /**
     * Kiểm tra xem tài khoản có bị khóa không
     */
    public boolean isAccountLocked(String usernameOrEmail) {
        String key = usernameOrEmail.toLowerCase();
        LoginAttempt attempt = loginAttempts.get(key);
        
        if (attempt == null) {
            return false;
        }
        
        if (attempt.isLocked()) {
            LocalDateTime now = LocalDateTime.now();
            long minutesPassed = ChronoUnit.MINUTES.between(attempt.getLockedAt(), now);
            
            if (minutesPassed > LOCK_TIME_MINUTES) {
                // Thời gian khóa hết, xóa khỏi danh sách
                loginAttempts.remove(key);
                return false;
            }
            return true;
        }
        
        return false;
    }

    /**
     * Inner class để lưu thông tin một login attempt
     */
    public static class LoginAttempt {
        private int attempts = 0;
        private LocalDateTime lockedAt;
        private boolean locked = false;

        public void incrementAttempts() {
            this.attempts++;
        }

        public void lock() {
            this.locked = true;
            this.lockedAt = LocalDateTime.now();
        }

        public int getAttempts() {
            return attempts;
        }

        public boolean isLocked() {
            return locked;
        }

        public LocalDateTime getLockedAt() {
            return lockedAt;
        }
    }
}
