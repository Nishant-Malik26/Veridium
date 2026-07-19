package com.veridium.auth_service.service;

import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.veridium.auth_service.constants.ErrorMessages.USER_NOT_FOUND_WITH_USERNAME;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                                  .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));

        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                                                            .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()
                                                                                                                .getName()))
                                                            .toList();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword_hash(), user.isEnabled(), true, true, true, authorities);

    }
}
