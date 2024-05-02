package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostsRepository extends JpaRepository<Posts, String> {
    Page<Posts> findByUserId(Users user, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Posts p WHERE p.id = :postId")
    void deleteByuserId(String postId);
    int countByUserId(Users user);
}