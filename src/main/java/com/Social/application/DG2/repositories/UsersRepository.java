package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    Users findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByMail(String mail);
    boolean existsByPhoneNumber(String phoneNumber);
    @Query("SELECT COUNT(u) FROM Users u JOIN u.followingUser f WHERE f.username = :username")
    int countByFollowingUsersUsername(@Param("username") String username);

    @Query("SELECT f FROM Users u JOIN u.followingUser f WHERE u.id = :userId ORDER BY u.username")
    Page<Users> findFollowingUsersByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT u FROM Users u JOIN u.followingUser f WHERE f.id = :userId ORDER BY u.username ASC")
    Page<Users> findFollowerUsersByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Users u JOIN u.favoritesPost p WHERE u.id = :userId AND p.id = :postId")
    boolean isPostFavoritedByUser(@Param("userId") String userId, @Param("postId") String postId);
}