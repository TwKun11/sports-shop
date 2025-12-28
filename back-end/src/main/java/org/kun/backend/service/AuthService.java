package org.kun.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kun.backend.component.JwtTokenUtils;
import org.kun.backend.dto.request.RegisterRequest;
import org.kun.backend.dto.response.AuthResponse;
import org.kun.backend.exception.InvalidCredentialsException;
import org.kun.backend.exception.ResourceNotFoundException;
import org.kun.backend.model.RefreshToken;
import org.kun.backend.model.Role;
import org.kun.backend.model.User;
import org.kun.backend.repository.RefreshTokenRepository;
import org.kun.backend.repository.RoleRepository;
import org.kun.backend.repository.UserRepository;
import org.kun.backend.util.LoginAttemptTracker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final JwtTokenUtils jwt;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final LoginAttemptTracker loginAttemptTracker;

    @org.springframework.beans.factory.annotation.Value("${jwt.expiration.access}")
    private long accessExpMs;

    // LOGIN
    @Transactional
    public AuthResponse login(String usernameOrEmail, String password) {
        log.info("Attempting login for user: {}", usernameOrEmail);
        
        // Kiểm tra account có bị lock không (chống brute force)
        if (loginAttemptTracker.isAccountLocked(usernameOrEmail)) {
            log.warn("Login failed: Account is locked - {}", usernameOrEmail);
            throw new InvalidCredentialsException("Account is temporarily locked due to too many failed attempts. Please try again later.");
        }
        
        // Kiểm tra user có tồn tại không
        User user = userRepo.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", usernameOrEmail);
                    // Ghi nhận failed attempt ngay cả khi user không tồn tại (không tiết lộ user có tồn tại)
                    loginAttemptTracker.recordFailedAttempt(usernameOrEmail);
                    return new ResourceNotFoundException("User not found");
                });

        // Kiểm tra trạng thái user
        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("Login failed: User is not active - {} (status: {})", usernameOrEmail, user.getStatus());
            loginAttemptTracker.recordFailedAttempt(usernameOrEmail);
            throw new InvalidCredentialsException("User account is not active");
        }

        // Xác thực thông tin đăng nhập
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );
            log.info("Authentication successful for user: {}", usernameOrEmail);
        } catch (BadCredentialsException e) {
            log.warn("Login failed: Invalid credentials for user - {}", usernameOrEmail);
            loginAttemptTracker.recordFailedAttempt(usernameOrEmail);
            throw new InvalidCredentialsException("Invalid username/email or password");
        } catch (AuthenticationException e) {
            log.warn("Login failed: Authentication error for user - {}: {}", usernameOrEmail, e.getMessage());
            loginAttemptTracker.recordFailedAttempt(usernameOrEmail);
            throw new InvalidCredentialsException("Authentication failed");
        }

        // Đăng nhập thành công - reset failed attempts
        loginAttemptTracker.recordSuccessfulAttempt(usernameOrEmail);

        // Kiểm tra và giới hạn số lượng token TRƯỚC KHI tạo token mới
        enforceTokenLimit(user);

        // Xóa refresh token cũ
        revokeAllUserTokens(user);

        // Tạo access + refresh mới
        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);
        
        // Lưu refresh token vào DB
        RefreshToken rf = RefreshToken.builder()
                .token(refresh)
                .user(user)
                .revoked(false)
                .expired(false)
                .createdAt(new Date())
                .build();

        refreshRepo.save(rf);
        log.info("Login successful for user: {}", usernameOrEmail);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(accessExpMs)
                .username(user.getUsername())
                .build();
    }

    // REGISTER
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting registration for user: {}", request.getUsername());
        
        // Kiểm tra username đã tồn tại chưa
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: Username already exists - {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Tìm role USER mặc định (hoặc tạo nếu chưa có)
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("USER")
                            .build();
                    return roleRepository.save(role);
                });

        // Tạo user mới
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(userRole)
                .status("ACTIVE")
                .build();

        user = userRepo.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        // Kiểm tra và giới hạn số lượng token TRƯỚC KHI tạo token mới
        enforceTokenLimit(user);

        // Tạo tokens
        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);
        
        // Lưu refresh token
        RefreshToken rf = RefreshToken.builder()
                .token(refresh)
                .user(user)
                .revoked(false)
                .expired(false)
                .createdAt(new Date())
                .build();

        refreshRepo.save(rf);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(accessExpMs)
                .username(user.getUsername())
                .build();
    }

    // REFRESH TOKEN
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        log.info("Attempting token refresh");
        
        RefreshToken stored = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Token refresh failed: Invalid refresh token");
                    return new ResourceNotFoundException("Invalid refresh token");
                });

        if (stored.isRevoked() || stored.isExpired() || jwt.isExpired(refreshToken)) {
            log.warn("Token refresh failed: Token is revoked/expired");
            throw new InvalidCredentialsException("Refresh token is invalid or expired");
        }

        User user = stored.getUser();

        // Kiểm tra user status trước khi refresh
        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("Token refresh failed: User is not active - {} (status: {})", user.getUsername(), user.getStatus());
            // Revoke token đã sử dụng
            stored.setRevoked(true);
            refreshRepo.save(stored);
            throw new InvalidCredentialsException("User account is not active");
        }

        // Kiểm tra và giới hạn số lượng token TRƯỚC KHI tạo token mới
        enforceTokenLimit(user);

        // Token rotation: Revoke token cũ
        stored.setRevoked(true);
        refreshRepo.save(stored);

        // Tạo access token mới
        String newAccess = jwt.generateAccessToken(user);
        
        // Tạo refresh token mới (rotation)
        String newRefresh = jwt.generateRefreshToken(user);
        
        // Lưu refresh token mới
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefresh)
                .user(user)
                .revoked(false)
                .expired(false)
                .createdAt(new Date())
                .build();
        
        refreshRepo.save(newRefreshToken);
        log.info("Token refreshed successfully for user: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .expiresIn(accessExpMs)
                .username(user.getUsername())
                .build();
    }

    // LOGOUT
    @Transactional
    public void logout(String refreshToken) {
        log.info("Attempting logout");
        
        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Logout failed: Invalid refresh token");
                    return new ResourceNotFoundException("Invalid refresh token");
                });
        
        token.setRevoked(true);
        refreshRepo.save(token);
        log.info("Logout successful for user: {}", token.getUser().getUsername());
    }

    // REVOKE ALL TOKENS
    @Transactional
    public void revokeAllTokens(String username) {
        log.info("Revoking all tokens for user: {}", username);
        
        User user = userRepo.findByUsernameOrEmail(username)
                .orElseThrow(() -> {
                    log.warn("Revoke failed: User not found - {}", username);
                    return new ResourceNotFoundException("User not found");
                });
        revokeAllUserTokens(user);
    }

    private void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshRepo.findAllValidTokenByUser(user.getId());
        tokens.forEach(t -> t.setRevoked(true));
        refreshRepo.saveAll(tokens);
    }
    
    // Kiểm tra và giới hạn số lượng token
    private void enforceTokenLimit(User user) {
        List<RefreshToken> validTokens = refreshRepo.findAllValidTokenByUser(user.getId());
        // Nếu vượt quá giới hạn, revoke token cũ nhất
        if (validTokens.size() >= 5) {
            // Sắp xếp theo thời gian tạo, revoke token cũ nhất
            validTokens.sort((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
            RefreshToken oldestToken = validTokens.get(0);
            oldestToken.setRevoked(true);
            refreshRepo.save(oldestToken);
        }
    }
}
