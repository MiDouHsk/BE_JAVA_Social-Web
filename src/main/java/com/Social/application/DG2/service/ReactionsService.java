package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.ReactionsDto;
import com.Social.application.DG2.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;



public interface ReactionsService {
    ResponseEntity<String> createReaction(ReactionsDto reactionDTO);
    void deleteReaction(String postId);
    int getReactionCountByIdPost(String object_id);
    int getReactionCountByTypeAndObjectId(String object_id, String type);
    void updateReaction(String reactionId, ReactionsDto updatedReactionDto);
    Page<Users> getUserByReaction(String objectId, String type, org.springframework.data.domain.Pageable pageable);
    Page<Users> getAllUsersInReactions(org.springframework.data.domain.Pageable pageable);
}
