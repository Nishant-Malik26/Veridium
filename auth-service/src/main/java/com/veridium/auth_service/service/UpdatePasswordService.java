package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.UpdatePasswordDto;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.user.*;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import com.veridium.auth_service.security.TenantAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    public void updatePassword(UpdatePasswordDto updatePasswordDto, TenantAuthenticationToken authenticationToken) {
        UserDetails userDetails = (UserDetails) authenticationToken.getPrincipal();
        String tenantSlug = authenticationToken.getTenantSlug();
        if(userDetails == null){
            throw new UserNotFoundException();
        }
        List<UserRole> userRoleList = userRoleRepository.findByUserEmailAndTenantSlug(userDetails.getUsername(), tenantSlug);
        if(userRoleList.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = userRoleList.getFirst().getUser();
        if(!passwordEncoder.matches(updatePasswordDto.oldPassword(), user.getPassword_hash())) {
            throw new OldPasswordNotCorrect();
        }
        String encodedNewPassword = passwordEncoder.encode(updatePasswordDto.newPassword());
        if(passwordEncoder.matches(updatePasswordDto.newPassword(), user.getPassword_hash())) {
            throw new NewPasswordCannotBeSameAsOldPassword();
        }
        if(!updatePasswordDto.confirmNewPassword().equals(updatePasswordDto.newPassword())){
            throw new PasswordNewPasswordNotMatch();
        }
        user.updatePassword(encodedNewPassword);
        userRepository.save(user);
    }

}
