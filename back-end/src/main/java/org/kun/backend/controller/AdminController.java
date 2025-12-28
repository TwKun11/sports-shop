package org.kun.backend.controller;

import lombok.RequiredArgsConstructor;
import org.kun.backend.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /**
     * Get dashboard statistics - Admin only
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 150);
        stats.put("totalOrders", 1250);
        stats.put("totalRevenue", 500000);
        stats.put("activeUsers", 45);
        
        return ResponseEntity.ok(
            ApiResponse.success("Dashboard statistics retrieved", stats)
        );
    }

    /**
     * Get all users - Admin only
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers() {
        Map<String, Object> data = new HashMap<>();
        data.put("users", new Object[]{});
        data.put("total", 0);
        
        return ResponseEntity.ok(
            ApiResponse.success("Users list retrieved", data)
        );
    }

    /**
     * Create new user - Admin only
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createUser(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", 1);
        response.putAll(user);
        response.put("status", "ACTIVE");
        
        return ResponseEntity.ok(
            ApiResponse.success("User created successfully", response)
        );
    }

    /**
     * Update user - Admin only
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", id);
        response.putAll(updates);
        response.put("message", "User updated successfully");
        
        return ResponseEntity.ok(
            ApiResponse.success("User updated", response)
        );
    }

    /**
     * Delete user - Admin only
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        response.put("userId", id.toString());
        
        return ResponseEntity.ok(
            ApiResponse.success("User deleted", response)
        );
    }

    /**
     * Get system logs - Admin only
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemLogs() {
        Map<String, Object> logs = new HashMap<>();
        logs.put("logs", new Object[]{});
        logs.put("total", 0);
        
        return ResponseEntity.ok(
            ApiResponse.success("System logs retrieved", logs)
        );
    }
}
