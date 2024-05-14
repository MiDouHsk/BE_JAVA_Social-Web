package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.OTP_ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTP_ResetPasswordRepository extends JpaRepository<OTP_ResetPassword, Long> {

    OTP_ResetPassword findByMailAndOtp(String mail, String otp);

}