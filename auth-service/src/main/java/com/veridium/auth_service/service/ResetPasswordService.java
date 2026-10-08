package com.veridium.auth_service.service;

import com.veridium.auth_service.config.RefreshTokenGenerator;
import com.veridium.auth_service.dto.RefreshTokenDto;
import com.veridium.auth_service.dto.ResetPasswordDto;
import com.veridium.auth_service.dto.UpdatePasswordDto;
import com.veridium.auth_service.entity.PasswordResetOtp;
import com.veridium.auth_service.entity.RefreshToken;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.exception.invitation.InvalidOtpException;
import com.veridium.auth_service.exception.invitation.OtpExpiredException;
import com.veridium.auth_service.exception.user.NewPasswordCannotBeSameAsOldPassword;
import com.veridium.auth_service.exception.user.PasswordNewPasswordNotMatch;
import com.veridium.auth_service.exception.user.TokenNotFoundException;
import com.veridium.auth_service.exception.user.UserNotFoundException;
import com.veridium.auth_service.repository.PasswordResetRepository;
import com.veridium.auth_service.repository.RefreshTokenRepository;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.utils.Hash;
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
    private final PasswordResetRepository passwordResetRepository;
    private final RefreshTokenGenerator  refreshTokenGenerator;

    @Transactional
    public RefreshTokenDto resetPassword(ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findById(resetPasswordDto.userId()).orElseThrow(UserNotFoundException::new);
        PasswordResetOtp passwordResetOtp = passwordResetRepository
                .findTopByUserIdAndIsUsedFalseOrderByCreatedAtDesc(resetPasswordDto.userId())
                .orElseThrow(InvalidOtpException::new);

        if (!Hash.verify(resetPasswordDto.token(), passwordResetOtp.getOtpHash())) {
            throw new InvalidOtpException();
        }

        if (passwordResetOtp.isExpired()) {
            throw new OtpExpiredException();
        }

        passwordResetOtp.setUsed(true);
        passwordResetRepository.save(passwordResetOtp);

        String encryptedPassword = passwordEncoder.encode(resetPasswordDto.newPassword());
        user.setPassword_hash(encryptedPassword);
        userRepository.save(user);


        refreshTokenRepository.revokeAllUserTokens(user.getId());

        String refreshToken = refreshTokenGenerator.generate();
        String refreshTokenHash = Hash.hashify(refreshToken);

        RefreshToken refreshTokenEntity = new RefreshToken(
                refreshTokenHash,
                user.getId(),
                resetPasswordDto.device(),
                resetPasswordDto.ip(),
                false
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new RefreshTokenDto(refreshToken);
    }
}
