package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResestRepository extends JpaRepository<PasswordResetOtp, Long> {
}
