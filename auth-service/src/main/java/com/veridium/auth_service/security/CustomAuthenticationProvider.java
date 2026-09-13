package com.veridium.auth_service.security;

import com.veridium.auth_service.service.CustomUserDetailsService;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder, CustomUserDetailsService  userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TenantAuthenticationToken authenticationToken =
                (TenantAuthenticationToken) authentication;
        String email = authentication.getName();

        String password =
                authentication.getCredentials().toString();

        String tenantSlug =
                authenticationToken.getTenantSlug();

        UserDetails userDetails =
                userDetailsService.loadUserByEmailAndTenant(
                        email,
                        tenantSlug
                );

        if (!passwordEncoder.matches(
                password,
                userDetails.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        if(!userDetails.isEnabled()){
            throw new DisabledException("User is disabled");
        }

        return new TenantAuthenticationToken(
                userDetails,
                null,
                tenantSlug,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TenantAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
