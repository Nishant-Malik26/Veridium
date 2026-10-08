package com.veridium.auth_service.exception.invitation;

import com.veridium.auth_service.constants.ErrorMessages;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException() {
        super(ErrorMessages.INVALID_OTP);
    }
}
