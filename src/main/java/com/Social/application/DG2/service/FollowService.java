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


    int countFollowingUsersById(String userId);
    int countFollowerUsersById(String userId);
    Page<UsersInfoDto> getFollowingListUsersById(String userId, Pageable pageable);
    Page<UsersInfoDto> getFollowerListUsersById(String userId, Pageable pageable);
}
