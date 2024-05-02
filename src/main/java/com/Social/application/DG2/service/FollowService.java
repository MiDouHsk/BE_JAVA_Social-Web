package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.UsersInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    void followUser(String followingUserId);

    int getFollowingCount();

    int getFollowerCount();
    void unfollowUser(String followingUserId);

    Page<UsersInfoDto> getFollowingListUsers(Pageable pageable);
    Page<UsersInfoDto> getFollowerListUsers(Pageable pageable);
    Page<UsersInfoDto> getNotFollowingListUsers(Pageable pageable);
}
