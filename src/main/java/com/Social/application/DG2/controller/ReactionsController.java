package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.ReactionsDto;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.service.ReactionsService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/reactions")
public class ReactionsController {

    private final ReactionsService reactionsService;

    public ReactionsController(ReactionsService reactionsService) {
        this.reactionsService = reactionsService;
    }

    @CheckLogin
    @PostMapping("/{object_type}/{postId}")
    public ResponseEntity<String> createReaction(@PathVariable String postId,
                                                 @PathVariable String object_type,
                                                 @RequestParam (defaultValue = "LIKE") String  type) {
        ReactionsDto reactionDto = new ReactionsDto();
        reactionDto.setObjectType(object_type);
        reactionDto.setObjectId(postId);
        reactionDto.setType(type);
        String result = String.valueOf(reactionsService.createReaction(reactionDto));
        return new ResponseEntity<>("Thêm thành công reactions.", HttpStatus.CREATED);
    }

    @CheckLogin
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deleteReaction(@PathVariable String postId) {
        try {
            reactionsService.deleteReaction(postId);
            return ResponseEntity.ok("Hủy thành công reactions.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CheckLogin
    @PutMapping("/{object_type}/{id}")
    public ResponseEntity<String> updateReaction(@PathVariable String id,
                                                 @PathVariable String object_type,
                                                 @RequestParam(defaultValue = "LIKE") String type) {
        ReactionsDto updatedReactionDto = new ReactionsDto();
        updatedReactionDto.setObjectType(object_type);
        updatedReactionDto.setObjectId(id);
        updatedReactionDto.setType(type);

        try {
            reactionsService.updateReaction(id, updatedReactionDto);
            return ResponseEntity.ok("Chỉnh sửa cảm xúc thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không thể xác định người dùng hiện tại.");
        }
    }

    @GetMapping("/count/{object_id}")
    public int getReactionCountByPostId(@PathVariable String object_id) {
        return reactionsService.getReactionCountByIdPost(object_id);
    }

    @GetMapping("/count/{object_id}/{type}")
    public int getReactionCountByTypeAndObjectId(@PathVariable String object_id, @PathVariable String type) {
        return reactionsService.getReactionCountByTypeAndObjectId(object_id, type);
    }

    @GetMapping("/users/{object_id}/{type}")
    public ResponseEntity<List<Users>> getUserByReaction(@PathVariable String object_id,
                                                         @PathVariable String type,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                         @RequestParam(defaultValue = "createAt") String sortName,
                                                         @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Users> usersPage = reactionsService.getUserByReaction(object_id, type, sortedByName);
            List<Users> usersList = usersPage.getContent();
            return ResponseEntity.ok(usersList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<Users>> getAllUsersInReactions(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(defaultValue = "createdBy") String sortName,
                                                              @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Users> usersPage = reactionsService.getAllUsersInReactions(pageable);
            List<Users> usersList = usersPage.getContent();
            return ResponseEntity.ok(usersList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}