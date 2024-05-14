package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.Reactions;
import com.Social.application.DG2.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionsRepository extends JpaRepository<Reactions, String> {
    List<Reactions> findAllByObjectIdAndType(String objectId, String type);
    Optional<Reactions> findByCreatedByAndObjectIdAndType(Users createdBy, String objectId, String type);
    List<Reactions> findByObjectId(String object_id);
    List<Reactions> findByObjectIdAndType(String objectId, String type);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM reactions r WHERE r.object_id = :objectId AND r.created_by = :userId", nativeQuery = true)
    void deleteByObjectIdAndCreatedBy(String objectId, String userId);
}
