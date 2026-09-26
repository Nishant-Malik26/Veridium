package com.veridium.auth_service.service;

import com.veridium.auth_service.constants.ErrorMessages;
import com.veridium.auth_service.dto.UpdatePasswordDto;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.user.UserNotFoundException;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import com.veridium.auth_service.security.TenantAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public String updatePassword(UpdatePasswordDto updatePasswordDto, TenantAuthenticationToken authenticationToken) {
        UserDetails userDetails = (UserDetails) authenticationToken.getPrincipal();
        String tenantSlug = authenticationToken.getTenantSlug();
        String oldPassword = userDetails.getPassword();
        if(oldPassword == null) {
            return HttpStatus.SERVICE_UNAVAILABLE.toString();
        }
        if(!oldPassword.equals(updatePasswordDto.oldPassword())){
            return ErrorMessages.ENTERED_OLD_PASSWORD_WRONG;
        }
        if(passwordEncoder.matches(updatePasswordDto.newPassword(), oldPassword)){
            return  ErrorMessages.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD_PASSWORD;
        }
        if(!updatePasswordDto.confirmNewPassword().equals(updatePasswordDto.newPassword())){
            return ErrorMessages.PASSWORD_AND_CONFIRM_PASSWORD_NOT_MATCH;
        }
        List<UserRole> userRoleList = userRoleRepository.findByUserEmailAndTenantSlug(userDetails.getUsername(), tenantSlug);
        if(userRoleList.isEmpty()){
            throw new UserNotFoundException();
        }
        User user = userRoleList.getFirst().getUser();
        user.updatePassword(updatePasswordDto.newPassword());
        userRepository.save(user);
        return null;
    }

}
