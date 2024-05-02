package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, String> {
    @Query("SELECT CONCAT(c.createBy.firstName, ' ', c.createBy.lastName)" +
            ", c.content, c.createBy.id" +
            ", c.createBy.avatar" +
            ", c.createAt" +
            ", c.totalLike" +
            " FROM Comments c WHERE c.postId.id = :postId ORDER BY c.createAt DESC")
    Page<Comments> findByPostId(@Param("postId") String postId, Pageable pageable);

    @Transactional
    @Query("DELETE FROM Comments c WHERE c.postId.id = :postId")
    @Modifying
    void deleteByPostId(@Param("postId") String postId);
}
