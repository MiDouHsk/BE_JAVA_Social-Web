package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.SearchUserDto;
import com.Social.application.DG2.dto.UsersDto;
import com.Social.application.DG2.service.Impl.UsersServiceImpl;
import com.Social.application.DG2.service.SearchUsersService;
import com.Social.application.DG2.util.annotation.CheckEnableType;
import com.Social.application.DG2.util.annotation.CheckLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Social.application.DG2.config.JwtTokenUtil;
import com.Social.application.DG2.dto.UsersInfoDto;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.service.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/user")
public class UsersController {
    @Autowired
    private UsersService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UsersServiceImpl registerService;
    @Autowired
    private SearchUsersService searchUsersService;
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpServletResponse response) {
        try{
            UserDetails userDetails = userService.login(username, password);
            String token = jwtTokenUtil.generateToken(username);
            Users users = userService.getUserByUsername(username);
            UsersInfoDto usersInfoDto = modelMapper.map(users, UsersInfoDto.class);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token: ", token);
            responseData.put("userInfo: ", usersInfoDto);

                return  ResponseEntity.ok(responseData);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> addNewUser(@RequestBody UsersDto registerDTO) {
        try {
            registerService.addUser(registerDTO);
            return ResponseEntity.ok("bạn đã tạo thành công tài khoản !");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo tai khoản: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchUserDto>> searchUsersByFullName(@RequestParam String fullName,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                                     @RequestParam(defaultValue = "createAt") String sortName,
                                                                     @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction;
            if (sortType.equalsIgnoreCase("ASC")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<SearchUserDto> usersPage = searchUsersService.findByFullNameContaining(fullName, sortedByName);
            return ResponseEntity.ok().body(usersPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CheckLogin
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UsersDto updatedUserDto) {
        try {
            ResponseEntity<String> response;
            response = userService.updateUser(updatedUserDto);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
    }

    @CheckLogin
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String username) {
        try {
            ResponseEntity<String> response;
            response = userService.deleteUser(username);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }

    @CheckLogin
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users users = userService.getUserByUsername(username);
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UsersInfoDto usersInfoDto = modelMapper.map(users, UsersInfoDto.class);
        return ResponseEntity.ok(usersInfoDto);
    }

}

