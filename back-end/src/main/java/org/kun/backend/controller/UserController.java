package org.kun.backend.controller;

import lombok.RequiredArgsConstructor;
import org.kun.backend.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    /**
     * Get current user profile - Accessible by authenticated users
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", "current_user");
        profile.put("email", "user@example.com");
        profile.put("role", "USER");
        
        return ResponseEntity.ok(
            ApiResponse.success("Profile retrieved successfully", profile)
        );
    }

    /**
     * Update user profile - Accessible by authenticated users
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(@RequestBody Map<String, String> updates) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("message", "Profile updated successfully");
        profile.putAll(updates);
        
        return ResponseEntity.ok(
            ApiResponse.success("Profile updated", profile)
        );
    }

    /**
     * Get user orders - Accessible by USER and ADMIN roles
     */
    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserOrders() {
        Map<String, Object> data = new HashMap<>();
        data.put("orders", new Object[]{});
        data.put("total", 0);
        
        return ResponseEntity.ok(
            ApiResponse.success("Orders retrieved", data)
        );
    }

    /**
     * Create order - Accessible by USER and ADMIN roles
     */
    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(@RequestBody Map<String, Object> order) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", 1);
        response.putAll(order);
        response.put("status", "CREATED");
        
        return ResponseEntity.ok(
            ApiResponse.success("Order created successfully", response)
        );
    }
}
