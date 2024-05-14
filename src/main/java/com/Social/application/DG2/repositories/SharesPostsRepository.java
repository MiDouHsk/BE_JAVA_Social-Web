package com.Social.application.DG2.repositories;

import com.Social.application.DG2.entity.SharesPosts;
import com.Social.application.DG2.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharesPostsRepository extends JpaRepository<SharesPosts, String> {

    List<SharesPosts> findByUserId(Users userId);

}