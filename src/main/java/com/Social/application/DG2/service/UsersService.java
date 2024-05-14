package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.UsersDto;
import com.Social.application.DG2.dto.UsersInfoDto;
import com.Social.application.DG2.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {
     UserDetails loadUserByUsername(String username);
     UserDetails login(String username, String password);
     Users getUserByUsername(String username);
    ResponseEntity<String> addUser(UsersDto registerDTO);
    ResponseEntity<String> updateUser(UsersDto updatedUserDto);
    ResponseEntity<String> deleteUser(String username);
    UsersInfoDto getUserById(String userId);

    ResponseEntity<String> updatePassword(String email, String newPassword);
}
