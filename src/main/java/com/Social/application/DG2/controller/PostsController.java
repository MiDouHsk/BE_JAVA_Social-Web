package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.PostsDto;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.service.MediaService;
import com.Social.application.DG2.service.PostsService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import com.Social.application.DG2.util.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/posts")
public class PostsController {
    private final PostsService postsService;
    @Autowired
    private MediaService postService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @CheckLogin
    @PostMapping("/create")
    public ResponseEntity<Posts> createPosts(@RequestBody PostsDto postDto) {
        Posts newPost = postsService.createPosts(postDto);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }
    @CheckLogin
    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable("postId") UUID postId, @RequestBody PostsDto updatedPost) {
        try {
            postsService.updatePost(postId, updatedPost);
            return new ResponseEntity<>("Post updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException | AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @CheckLogin
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId") UUID postId) {
        try {
            postsService.deletePost(postId);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException | AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while deleting the post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/userList/{userId}")
    public ResponseEntity<Page<Posts>> getPostsByUserId(@PathVariable("userId") UUID userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int pageSize,
                                                        @RequestParam(defaultValue = "createAt") String sortName,
                                                        @RequestParam(defaultValue = "DESC") String sortType
    ) {
        try{
            Sort.Direction direction;
            if (sortType.equalsIgnoreCase("ASC")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Posts> posts = postsService.getPostsByUserId(sortedByName, userId);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/allList")
    public ResponseEntity<Page<Posts>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(defaultValue = "createAt") String sortName,
                                                   @RequestParam(defaultValue = "DESC") String sortType
    ) {
        try {
            Sort.Direction direction;
            if (sortType.equalsIgnoreCase("ASC")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Posts> posts = postsService.getAllPosts(sortedByName);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/allCount")
    public ResponseEntity<Integer> getNumberOfPosts() {
        int numberOfPosts = postsService.getNumberOfPosts();
        return new ResponseEntity<>(numberOfPosts, HttpStatus.OK);
    }

    @GetMapping("/userCount/{userId}")
    public ResponseEntity<Integer> getNumberOfPostsByUserId(@PathVariable("userId") UUID userId) {
        int numberOfPosts = postsService.getNumberOfPostsByUserId(userId);
        return ResponseEntity.ok(numberOfPosts);
    }

    @CheckLogin
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadPostVideo(@RequestParam("filePath") MultipartFile file) {
        try {
            String mediaId = postService.uploadMedia(file);
            return ResponseEntity.ok(mediaId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    @CheckLogin
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePostVideos(@RequestParam("filePath") String filePath) {
        try {
            postService.deletePost(filePath);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting file: " + e.getMessage());
        }
    }

    @CheckLogin
    @GetMapping("/userCount")
    public ResponseEntity<Integer> getNumberOfPostsByLoggedInUser() {
        try {
            int numberOfPosts = postsService.getNumberOfPostsByLoggedInUser();
            return ResponseEntity.ok(numberOfPosts);
        } catch (EntityNotFoundException ex) {
            throw new NotFoundException("User not found"+ ex);
        }
    }

    @CheckLogin
    @GetMapping("/userList")
    public ResponseEntity<Page<Posts>> getListOfPostsByLoggedInUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType
    ) {
        try {
            Sort.Direction direction;
            if (sortType.equalsIgnoreCase("ASC")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Posts> posts = postsService.getListOfPostsByLoggedInUser(sortedByName);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}