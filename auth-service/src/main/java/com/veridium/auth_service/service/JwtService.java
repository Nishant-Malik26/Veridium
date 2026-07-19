package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.TenantDto;
import com.veridium.auth_service.dto.UserDto;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.role.RoleNotFoundException;
import com.veridium.auth_service.repository.UserRoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    //private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    //private final TenantRepository tenantRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                   .verifyWith(getSigningKey())
                   .build()
                   .parseSignedClaims(jwt)
                   .getPayload();
    }

    public <T> T extractClaim(String jwt, String claimName, Class<T> requiredType) {
        return extractAllClaims(jwt).get(claimName, requiredType);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        String username = extractClaim(jwt, "email", String.class);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    public boolean isTokenExpired(String jwt) {
        return extractClaim(jwt, "expiration", Date.class).before(new Date());
    }

    public String createJwtWithClaims(UserDto user, TenantDto tenant) {
        Map<String, Object> claims = new HashMap<>();
        UserRole userRole = userRoleRepository.findByUserIdAndTenantId(user.id(), tenant.id())
                                              .orElseThrow(RoleNotFoundException::new);

        claims.put("userId", user.id());
        claims.put("email", user.email());
        claims.put("tenantId", tenant.id());
        claims.put("tenantSlug", tenant.slug());
        claims.put("role", userRole.getRole()
                                   .getName());
        return Jwts.builder()
                   .claims(claims)
                   .subject(user.email())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(getSigningKey())
                   .compact();
    }
}

