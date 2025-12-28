package org.kun.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kun.backend.dto.request.LoginRequest;
import org.kun.backend.dto.request.RefreshTokenRequest;
import org.kun.backend.dto.request.RegisterRequest;
import org.kun.backend.dto.response.ApiResponse;
import org.kun.backend.dto.response.AuthResponse;
import org.kun.backend.service.AuthService;
import org.kun.backend.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse httpResponse) {
        AuthResponse response = authService.register(request);
        
        // Set refresh token vào httpOnly cookie (enterprise best practice)
        cookieUtil.setRefreshTokenCookie(httpResponse, response.getRefreshToken());
        
        // Không trả về refreshToken trong response body (security)
        response.setRefreshToken(null);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {
        AuthResponse response = authService.login(request.getUsernameOrEmail(), request.getPassword());
        
        // Set refresh token vào httpOnly cookie (enterprise best practice)
        cookieUtil.setRefreshTokenCookie(httpResponse, response.getRefreshToken());
        
        // Không trả về refreshToken trong response body (security)
        response.setRefreshToken(null);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        // Đọc refresh token từ httpOnly cookie (enterprise best practice)
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Refresh token not found", "/api/auth/refresh"));
        }
        
        AuthResponse response = authService.refresh(refreshToken);
        
        // Set refresh token mới vào cookie
        cookieUtil.setRefreshTokenCookie(httpResponse, response.getRefreshToken());
        
        // Không trả về refreshToken trong response body (security)
        response.setRefreshToken(null);
        
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        // Đọc refresh token từ cookie
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(httpRequest);
        
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(refreshToken);
        }
        
        // Xóa refresh token cookie
        cookieUtil.deleteRefreshTokenCookie(httpResponse);
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @PostMapping("/revoke-all")
    public ResponseEntity<ApiResponse<Object>> revokeAllTokens() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
        authService.revokeAllTokens(username);
        return ResponseEntity.ok(ApiResponse.success("All tokens revoked successfully", null));
    }
}

