package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.entity.OTP_ResetPassword;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.OTP_ResetPasswordRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.EmailService;
import com.Social.application.DG2.service.OTPService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;

@Service
public class OTP_ResetPasswordServiceImpl implements OTPService, EmailService {
    private final JavaMailSender javaMailSender;
    private final OTP_ResetPasswordRepository otpRepository;
    private final UsersRepository userRepository;
    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

    public OTP_ResetPasswordServiceImpl(JavaMailSender javaMailSender, OTP_ResetPasswordRepository otpRepository, UsersRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;

    }

    //    Phương thức gửi email.
    @Override
    public void sendEmail(String to, String subject, String content) {
        logger.info("Đang gửi email đến: {}", to);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(content, "text/html; charset=UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            javaMailSender.send(mimeMessage);
            logger.info("Email đã được gửi thành công đến: {}", to);
        } catch (MessagingException e) {
            logger.error("Gửi email đến '{}' thất bại.", to, e);
            throw new RuntimeException("Gửi email thất bại.");
        }
    }

    //    Phương thức tạo mã OTP và gửi email xác nhận cho người dùng.
    @Override
    public String generateOTPAndSendEmail(String mail) {
        logger.info("Đang tạo mã OTP và gửi email xác nhận cho người dùng với email: {}", mail);
        Users user = userRepository.findByEmail(mail);
        if (user == null) {
            logger.error("Không tìm thấy người dùng với email: {}", mail);
            throw new RuntimeException("Không tìm thấy người dùng với email: " + mail);
        }
        String otp = generateOTP();
        String fullName = user.getLastName();
        String subject = "OTP Confirmation";
        String emailContent = generateOTPContent(fullName, otp);
        sendEmail(mail, subject, emailContent);
        saveOTP(mail, otp, calculateExpirationTime());
        logger.info("Mã OTP đã được tạo và gửi thành công đến email: {}", mail);
        return otp;
    }

    //    Phương thức lưu mã OTP vào cơ sở dữ liệu.
    @Override
    public void saveOTP(String mail, String otp, Timestamp expirationTime) {
        logger.info("Đang lưu mã OTP cho email: {}", mail);
        OTP_ResetPassword otpEntity = new OTP_ResetPassword();
        otpEntity.setMail(mail);
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        logger.info("Mã OTP đã được lưu thành công cho email: {}", mail);
    }

    //    Phương thức tạo mã OTP.
    @Override
    public String generateOTP() {
        logger.info("Đang tạo mã OTP mới");
        final String OTP_CHARACTERS = "0123456789";
        final int OTP_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARACTERS.charAt(random.nextInt(OTP_CHARACTERS.length())));
        }
        logger.info("Mã OTP mới đã được tạo: {}", otp.toString());
        return otp.toString();
    }

    //    Phương thức xác thực mã OTP.
    @Override
    public ResponseEntity<String> validateOTP(String mail, String otp) {
        logger.info("Xác thực OTP cho email: {}", mail);
        OTP_ResetPassword otpEntity = otpRepository.findByMailAndOtp(mail, otp);
        if (otpEntity == null) {
            logger.warn("OTP không hợp lệ cho email: {}", mail);
            return new ResponseEntity<>("OTP không hợp lệ.", HttpStatus.BAD_REQUEST);
        }
        if (otpEntity.isUsed()) {
            logger.warn("OTP đã được sử dụng cho email: {}", mail);
            return new ResponseEntity<>("OTP đã được sử dụng.", HttpStatus.BAD_REQUEST);
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp expirationTime = otpEntity.getExpirationTime();
        if (currentTime.after(expirationTime)) {
            logger.warn("OTP đã hết hạn cho email: {}", mail);
            return new ResponseEntity<>("OTP đã hết hạn.", HttpStatus.BAD_REQUEST);
        }
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        logger.info("OTP đã được xác thực thành công cho email: {}", mail);
        return new ResponseEntity<>("OTP hợp lệ.", HttpStatus.OK);
    }

    //    Phương thức tạo nội dung email chứa mã OTP.
    private String generateOTPContent(String fullName, String otp) {
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><body style='font-family: Arial, sans-serif;'>");
        emailContent.append("<h2 style='color: #333333;'>Hello ").append(fullName).append(",</h2>");
        emailContent.append("<p style='color: #333333;'>We have received a password reset request from you. Below is your OTP.</p>");
        emailContent.append("<p style='color: #333333;'><strong>Your OTP:</strong> ").append(otp).append("</p>");
        emailContent.append("<p style='color: #333333;'>Please use this OTP to reset your password.</p>");
        emailContent.append("<p style='color: #333333;'>Please do not share this code with anyone.</p>");
        emailContent.append("<p style='color: #333333;'>Best regards,<br>");
        emailContent.append("Social Application Team</p>");
        emailContent.append("<p style='color: #333333;'>Email: huydung446@gmail.com<br>");
        emailContent.append("Hotline: 0123 456 789<br>");
        emailContent.append("Working hours: Monday - Friday, 8:00 AM - 5:00 PM</p>");
        emailContent.append("</body></html>");
        return emailContent.toString();
    }

    //    Phương thức tính thời gian hết hạn của mã OTP.
    private Timestamp calculateExpirationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (OTP_EXPIRATION_MINUTES * 60 * 1000);
        return new Timestamp(expirationTimeMillis);
    }
}