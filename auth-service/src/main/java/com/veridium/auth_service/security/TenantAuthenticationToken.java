package com.veridium.auth_service.security;

import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


public class TenantAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String tenantSlug;


    // Before authentication
    public TenantAuthenticationToken(
            String email,
            String password,
            String tenantSlug) {

        super(email, password);
        this.tenantSlug = tenantSlug;
    }

    // After authentication
    public TenantAuthenticationToken(
            Object principal,
            Object credentials,
            String tenantSlug,
            Collection<? extends GrantedAuthority> authorities) {

        super(principal, credentials, authorities);
        this.tenantSlug = tenantSlug;
    }

    public String getTenantSlug() {
        return tenantSlug;
    }
}
