package com.veridium.auth_service.service;

import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.veridium.auth_service.constants.ErrorMessages.USER_NOT_FOUND_WITH_USERNAME;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {
    private final UserRoleRepository userRoleRepository;

    public UserDetails loadUserByEmailAndTenant(String username, String tenantSlug) throws UsernameNotFoundException {

        List<UserRole> userRoles = userRoleRepository.findByUserEmailAndTenantSlug(username, tenantSlug);
        if(userRoles.isEmpty()) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_WITH_USERNAME);
        }
        User user = userRoles.getFirst().getUser();

        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                                                            .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()
                                                                                                                .getName()))
                                                            .toList();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword_hash(), user.isEnabled(), true, true, true, authorities);

    }
}
