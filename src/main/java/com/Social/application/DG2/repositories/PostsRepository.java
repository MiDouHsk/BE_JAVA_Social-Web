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

    @Query("SELECT p FROM Posts p JOIN p.favoritesUser u WHERE u.id = :userId")
    Page<Posts> findFavoritesByUserId(String userId, Pageable pageable);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM favorites f WHERE f.user_id = :userId AND f.post_id = :postId", nativeQuery = true)
    void deleteFavoriteByUserIdAndPostId(String userId, String postId);

    @Query(value = "SELECT COUNT(*) FROM favorites WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    int countFavoritesByUserIdAndPostId(String userId, String postId);
}