package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.Favorites;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, String> {

    Page<Favorites> findByUserID(Users user, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM favorites f WHERE f.post_id = :postId", nativeQuery = true)
    void deleteByPostId(@Param("postId") String postId);

    Optional<Favorites> findByUserIDAndPostsID(Users user, Posts post);

}
