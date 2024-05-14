package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.UsersInfoDto;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.FollowService;
import com.Social.application.DG2.service.UsersService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private UsersRepository usersRepository;

    @CheckLogin
    @PostMapping("/user/{followingUserId}")
    public ResponseEntity<String> followUser(@PathVariable String followingUserId) {
        followService.followUser(followingUserId);
        return new ResponseEntity<>("User followed successfully", HttpStatus.OK);
    }
    @CheckLogin
    @GetMapping("/followingCount")
    public int getFollowingCount() {
        return followService.getFollowingCount();
    }
    @CheckLogin
    @GetMapping("/followerCount")
    public int getFollowerCount() {
        return followService.getFollowerCount();
    }
    @CheckLogin
    @GetMapping("/ListUsers/following")
    public ResponseEntity<Page<UsersInfoDto>> getFollowingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers = followService.getFollowingListUsers(pageable);
        return ResponseEntity.ok(followerUsers);
    }

    @CheckLogin
    @GetMapping("/ListUsers/follower")
    public ResponseEntity<Page<UsersInfoDto>> getFollowerUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers = followService.getFollowerListUsers(pageable);
        return ResponseEntity.ok(followerUsers);
    }

    @CheckLogin
    @GetMapping("/ListUsers/notFollowing")
    public ResponseEntity<List<UsersInfoDto>> getNotFollowingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> notFollowingUsers =
                followService.getNotFollowingListUsers(pageable);
        return ResponseEntity.ok(notFollowingUsers.getContent());
    }
    @GetMapping("/followingCount/{userId}")
    public int getFollowingCountByUserId(@PathVariable String userId) {
        return followService.countFollowingUsersById(userId);
    }

    @GetMapping("/followerCount/{userId}")
    public int getFollowerCountByUserId(@PathVariable String userId) {
        return followService.countFollowerUsersById(userId);
    }

    @GetMapping("/ListUsers/follower/{userId}")
    public ResponseEntity<List<UsersInfoDto>> getFollowerListUsersById(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers =
                followService.getFollowerListUsersById(userId, pageable);
        return ResponseEntity.ok(followerUsers.getContent());
    }

    @GetMapping("/ListUsers/following/{userId}")
    public ResponseEntity<List<UsersInfoDto>> getFollowingListUsersById(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers =
                followService.getFollowingListUsersById(userId, pageable);
        return ResponseEntity.ok(followerUsers.getContent());
    }



    @DeleteMapping("/user/unfollow/{followingUserId}")
    public ResponseEntity<String> unfollowUser(@PathVariable String followingUserId) {
        followService.unfollowUser(followingUserId);
        return new ResponseEntity<>("User unfollowed successfully", HttpStatus.OK);
    }
}
