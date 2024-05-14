package com.Social.application.DG2.dto;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class OTP_ResetPasswordDto {
        private String mail;
        private String otp;
        private Timestamp expirationTime;

}
