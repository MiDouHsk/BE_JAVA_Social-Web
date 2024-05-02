package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.UsersDto;
import com.Social.application.DG2.entity.Enum.EnableType;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.service.UsersService;
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

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersRepository registerRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users users = userRepository.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(users);
    }

    @Override
    public ResponseEntity<String> addUser(UsersDto registerDTO) {
        if (registerRepository.existsByUsername(registerDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username đã đợc sử dụng.");
        }
        if (registerRepository.existsByMail(registerDTO.getMail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
        }
        if (registerRepository.existsByPhoneNumber(registerDTO.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
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

        return ResponseEntity.ok("Tạo thành công với username: " + user.getUsername());
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
        return ResponseEntity.ok("Cập nhật thành công username: " + existingUser.getUsername());
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
}