package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.ResetPasswordDto;
import com.veridium.auth_service.dto.UpdatePasswordDto;
import com.veridium.auth_service.entity.RefreshToken;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.exception.user.NewPasswordCannotBeSameAsOldPassword;
import com.veridium.auth_service.exception.user.PasswordNewPasswordNotMatch;
import com.veridium.auth_service.exception.user.TokenNotFoundException;
import com.veridium.auth_service.repository.RefreshTokenRepository;
import com.veridium.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(resetPasswordDto.token()).orElseThrow(TokenNotFoundException::new);
        if(refreshToken.isRevoked()){
            throw new TokenNotFoundException();
        }
        User user = userRepository.findById(refreshToken.getUserId()).orElseThrow(TokenNotFoundException::new);
        if(!resetPasswordDto.confirmPassword().equals(resetPasswordDto.newPassword())){
            throw new PasswordNewPasswordNotMatch();
        }
        user.updatePassword(passwordEncoder.encode(resetPasswordDto.newPassword()));
        userRepository.save(user);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return true;
    }

    public boolean updatePassword(UpdatePasswordDto updatePasswordDto) {
        if(updatePasswordDto.oldPassword().equals(updatePasswordDto.newPassword())){
            throw new NewPasswordCannotBeSameAsOldPassword();
        }
        if(!updatePasswordDto.newPassword().equals(updatePasswordDto.confirmNewPassword())){
            throw new PasswordNewPasswordNotMatch();
        }



        return true;
    }
}
