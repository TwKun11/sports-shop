package org.kun.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kun.backend.component.JwtTokenUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwt;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain
    ) throws IOException, ServletException {

        final String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            log.debug("No JWT token found in Authorization header");
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = auth.substring(7);
            
            // Kiểm tra token hợp lệ trước khi extract
            if (!jwt.isValid(token)) {
                log.warn("Invalid or expired JWT token");
                chain.doFilter(request, response);
                return;
            }

            String username = jwt.extractUsername(token);

            // Kiểm tra token chưa được set vào context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details và kiểm tra trạng thái
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Kiểm tra user có enabled và không bị locked không
                if (!userDetails.isEnabled()) {
                    log.warn("User account is disabled: {}", username);
                    chain.doFilter(request, response);
                    return;
                }
                
                if (!userDetails.isAccountNonLocked()) {
                    log.warn("User account is locked: {}", username);
                    chain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("JWT token validated and authentication set for user: {}", username);
            }
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
            // Cho phép request tiếp tục, Spring Security sẽ xử lý khi endpoint cần auth
            chain.doFilter(request, response);
        } catch (JwtException ex) {
            log.warn("JWT token validation failed: {}", ex.getMessage());
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Cannot set user authentication in security context: {}", ex.getMessage(), ex);
            // Không throw exception, cho phép request tiếp tục
            // GlobalExceptionHandler sẽ xử lý khi endpoint cần authentication
            chain.doFilter(request, response);
        }

        chain.doFilter(request, response);
    }
}
