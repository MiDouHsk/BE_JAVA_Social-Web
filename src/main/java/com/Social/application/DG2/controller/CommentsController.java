package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.CommentsDto;
import com.Social.application.DG2.entity.Comments;
import com.Social.application.DG2.service.CommentsService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @PostMapping("/create")
    public Comments addComment(@RequestParam String postId, @RequestParam String content) {
        CommentsDto comment = new CommentsDto();
        comment.setPostId(postId);
        comment.setContent(content);

        return commentsService.saveComment(comment);
    }

    @CheckLogin
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<Comments>> getCommentsByPostId(@PathVariable String postId,
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
        Page<Comments> commentsPage = commentsService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(commentsPage);
    }

    @CheckLogin
    @PutMapping("/update")
    public void update(@RequestParam String id, @RequestParam String content) {
        CommentsDto comment = new CommentsDto();
        comment.setId(id);
        comment.setContent(content);
        commentsService.updateComments(comment);
    }
    @CheckLogin
    @DeleteMapping("/delete/{commentId}")
    public void deleteComment(@PathVariable UUID commentId) {
        commentsService.deleteComment(commentId);
    }
}
