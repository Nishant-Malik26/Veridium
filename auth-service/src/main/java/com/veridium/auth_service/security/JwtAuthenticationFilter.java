package com.veridium.auth_service.security;

import com.veridium.auth_service.service.CustomUserDetailsService;
import com.veridium.auth_service.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.veridium.auth_service.constants.Constants.PUBLIC_ENDPOINTS;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestPath = request.getServletPath();

            if (PUBLIC_ENDPOINTS.stream()
                                .anyMatch(requestPath::startsWith)) {
                filterChain.doFilter(request, response);
                return;
            }
            String authorizationToken = request.getHeader("Authorization");
            if (!StringUtils.hasText(authorizationToken) || !authorizationToken.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = authorizationToken.substring(7);
            String tenantSlug = jwtService.extractClaim(jwt, "tenantSlug", String.class);
            TenantContext.setCurrentTenant(tenantSlug);
            String email = jwtService.extractClaim(jwt, "email", String.class);
            if (email != null && SecurityContextHolder.getContext()
                                                      .getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext()
                                         .setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
        } finally {
            TenantContext.clear();
        }
    }
}
