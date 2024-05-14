package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.PostsDto;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostsService {
    Posts createPosts(PostsDto post);
    void updatePost(UUID postId, PostsDto updatedPost);
    void deletePost(UUID postId);

    Page<Posts> getPostsByUserId(Pageable pageable, UUID userId);
    int getNumberOfPostsByUserId(UUID userId);

    Page<Posts> getAllPosts(Pageable pageable);
    int getNumberOfPosts();

    Page<Posts> getListOfPostsByLoggedInUser(Pageable pageable);
    int getNumberOfPostsByLoggedInUser();
}