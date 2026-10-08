package com.veridium.auth_service.exception.invitation;

import com.veridium.auth_service.constants.ErrorMessages;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException() {
        super(ErrorMessages.OTP_EXPIRED);
    }
}
