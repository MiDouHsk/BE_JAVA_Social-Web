package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.SharesPostDto;
import com.Social.application.DG2.entity.SharesPosts;
import com.Social.application.DG2.service.SharesPostService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/shares")
public class SharesPostsController {
    @Autowired
    private ModelMapper modelMapper;

    private final SharesPostService sharesPostService;

    @Autowired
    public SharesPostsController(SharesPostService sharesPostService) {
        this.sharesPostService = sharesPostService;
    }

    @CheckLogin
    @PostMapping("/post/{postId}")
    public ResponseEntity<String> createSharePost(@PathVariable String postId, Authentication authentication) {
        String currentUsername = authentication.getName();
        SharesPostDto sharesPostDto = new SharesPostDto();
        sharesPostDto.setPostDtoId(postId);
        sharesPostDto.setUserDtoId(currentUsername);
        sharesPostService.createdSharePost(sharesPostDto);
        return ResponseEntity.ok("Bài viết đã được chia sẻ thành công.");
    }


    @CheckLogin
    @DeleteMapping("/{sharesPostId}")
    public ResponseEntity<String> deleteSharedPost(@PathVariable String sharesPostId, Authentication authentication) {
        String currentUsername = authentication.getName();
        sharesPostService.deleteSharedPost(sharesPostId, currentUsername);
        return ResponseEntity.ok("Bài viết đã được xóa khỏi danh sách đã chia sẻ.");
    }

    @CheckLogin
    @GetMapping("/current-user")
    public ResponseEntity<List<SharesPosts>> getSharedPostsByCurrentUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        List<SharesPosts> sharesPosts = sharesPostService.getSharedPostByCurrentUser(currentUsername);
        return ResponseEntity.ok(sharesPosts);
    }

    @ExceptionHandler({NotFoundException.class, AuthenticationCredentialsNotFoundException.class, UnauthorizedException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SharesPosts>> getSharedPostsByUserId(@PathVariable String userId) {
        try {
            List<SharesPosts> sharesPosts = sharesPostService.getSharedPostsByUserId(userId);
            return ResponseEntity.ok(sharesPosts);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}