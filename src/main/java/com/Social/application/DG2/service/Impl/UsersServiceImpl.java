package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.UsersDto;
import com.Social.application.DG2.dto.UsersInfoDto;
import com.Social.application.DG2.entity.Enum.EnableType;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.service.UsersService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.Social.application.DG2.config.CustomUserDetails;
import com.Social.application.DG2.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersRepository registerRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UsersRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    // Kiểm tra xem mật khẩu có đáp ứng các yêu cầu không
    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public UsersServiceImpl(UsersRepository registerRepository, PasswordEncoder encoder, UsersRepository userRepository) {
        this.registerRepository = registerRepository;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users users = userRepository.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(users);
    }

    @Override
    public ResponseEntity<String> addUser(UsersDto registerDTO)  {
        logger.info("Đang thêm người dùng với tên đăng nhập: {}", registerDTO.getUsername());

        if (registerRepository.existsByUsername(registerDTO.getUsername())) {
            logger.warn("Tên người dùng '{}' đã tồn tại", registerDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên người dùng đã tồn tại.");
        }

        if (registerRepository.existsByMail(registerDTO.getMail())) {
            logger.warn("Email '{}' đã được sử dụng", registerDTO.getMail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã được sử dụng.");
        }

        if (registerRepository.existsByPhoneNumber(registerDTO.getPhoneNumber())) {
            logger.warn("Số điện thoại '{}' đã được sử dụng", registerDTO.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Số điện thoại đã được sử dụng.");
        }

        // Kiểm tra mật khẩu
        if (!isPasswordValid(registerDTO.getPassword())) {
            logger.warn("Mật khẩu không đáp ứng yêu cầu: mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }
        Users user = new Users();

        user.setUsername(registerDTO.getUsername());
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setGender(registerDTO.isGender());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setDateOfBirth(registerDTO.getDateOfBirth());
        user.setAddress(registerDTO.getAddress());
        user.setMail(registerDTO.getMail());

        registerRepository.save(user);

        logger.info("Người dùng '{}' đã được tạo thành công", user.getUsername());
        return ResponseEntity.ok("Tạo thành công với tên đăng nhập: " + user.getUsername());
    }

    @Override
    public UserDetails login(String username, String password) {
        String normalizedUsername = username.toLowerCase();
        UserDetails userDetails = loadUserByUsername(normalizedUsername);
        if(!encoder.matches(password,userDetails.getPassword())) {
            throw new BadCredentialsException("sai tài khoản hoặc mật khẩu.");
        }
        return userDetails;
    }

    @Override
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public ResponseEntity<String> updateUser(UsersDto updatedUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users users = userRepository.findByUsername(currentUsername);

        if (!users.getUsername().equals(updatedUserDto.getUsername())) {
            logger.warn("Người dùng '{}' không được phép sửa thông tin người dùng khác.", currentUsername);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền sửa đổi thông tin người dùng khác.");
        }

        Users existingUser = userRepository.findByUsername(updatedUserDto.getUsername());

        existingUser.setFirstName(updatedUserDto.getFirstName());
        existingUser.setLastName(updatedUserDto.getLastName());
        existingUser.setGender(updatedUserDto.isGender());
        existingUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        existingUser.setDateOfBirth(updatedUserDto.getDateOfBirth());
        existingUser.setAddress(updatedUserDto.getAddress());
        existingUser.setMail(updatedUserDto.getMail());

        registerRepository.save(existingUser);
        logger.info("Thông tin của người dùng '{}' đã được cập nhật thành công", existingUser.getUsername());
        return ResponseEntity.ok("Cập nhật thành công usernames : " + existingUser.getUsername());
    }

    @Override
    public ResponseEntity<String> deleteUser(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Users users = userRepository.findByUsername(currentUsername);
        Users userDelete = userRepository.findByUsername(username);

        if (!users.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xóa người dùng khác.");
        }
        userDelete.setEnableType(EnableType.FALSE);
        userRepository.save(userDelete);
        return ResponseEntity.ok("Bạn đã xóa thành công tài khoản của mình. ");
    }

    @Override
    public UsersInfoDto getUserById(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return convertToDto(user);
    }

    @Override
    public ResponseEntity<String> updatePassword(String email, String newPassword) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("Người dùng với email '{}' không tồn tại", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }
        String encryptedPassword = encoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        logger.info("Mật khẩu của người dùng với email '{}' đã được cập nhật thành công", email);
        return ResponseEntity.ok("Mật khẩu cập nhật thành công.");
    }

    private UsersInfoDto convertToDto(Users user) {
        UsersInfoDto usersInfoDto = new UsersInfoDto();
        usersInfoDto.setId(String.valueOf(user.getId()));
        usersInfoDto.setUsername(user.getUsername());
        usersInfoDto.setFirstName(user.getFirstName());
        usersInfoDto.setLastName(user.getLastName());
        usersInfoDto.setGender(user.isGender());
        usersInfoDto.setPhoneNumber(user.getPhoneNumber());
        usersInfoDto.setDateOfBirth(user.getDateOfBirth());
        usersInfoDto.setMail(user.getMail());
        usersInfoDto.setAddress(user.getAddress());
        usersInfoDto.setAvatar(user.getAvatar());
        usersInfoDto.setBackground(user.getBackground());
        return usersInfoDto;
    }

}