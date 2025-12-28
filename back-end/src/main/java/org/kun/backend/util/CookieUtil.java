package org.kun.backend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class để quản lý cookies cho refresh token
 * Enterprise best practice: httpOnly, Secure, SameSite cookies
 */
@Component
@Slf4j
public class CookieUtil {

    @Value("${jwt.cookie.name:refreshToken}")
    private String cookieName;

    @Value("${jwt.cookie.max-age:604800}") // 7 days in seconds
    private int cookieMaxAge;

    @Value("${jwt.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.http-only:true}")
    private boolean cookieHttpOnly;

    @Value("${jwt.cookie.same-site:Strict}")
    private String cookieSameSite;

    @Value("${jwt.cookie.path:/}")
    private String cookiePath;

    /**
     * Set refresh token vào httpOnly cookie
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setSecure(cookieSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(cookieMaxAge);
        
        // SameSite attribute (cần set qua response header vì Cookie class không hỗ trợ)
        String sameSiteValue = "SameSite=" + cookieSameSite;
        response.addHeader("Set-Cookie", 
            String.format("%s=%s; Path=%s; Max-Age=%d; HttpOnly; %s; %s",
                cookieName,
                refreshToken,
                cookiePath,
                cookieMaxAge,
                cookieSecure ? "Secure" : "",
                sameSiteValue
            )
        );
        
        log.debug("Set refresh token cookie: {} (httpOnly={}, secure={}, sameSite={})", 
            cookieName, cookieHttpOnly, cookieSecure, cookieSameSite);
    }

    /**
     * Lấy refresh token từ cookie
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                log.debug("Found refresh token cookie: {}", cookieName);
                return cookie.getValue();
            }
        }
        
        log.debug("Refresh token cookie not found: {}", cookieName);
        return null;
    }

    /**
     * Xóa refresh token cookie (logout)
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setSecure(cookieSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0); // Xóa cookie
        
        // Set header để xóa cookie
        String sameSiteValue = "SameSite=" + cookieSameSite;
        response.addHeader("Set-Cookie", 
            String.format("%s=; Path=%s; Max-Age=0; HttpOnly; %s; %s",
                cookieName,
                cookiePath,
                cookieSecure ? "Secure" : "",
                sameSiteValue
            )
        );
        
        log.debug("Deleted refresh token cookie: {}", cookieName);
    }
}

