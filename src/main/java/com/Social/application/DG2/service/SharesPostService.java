package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.SharesPostDto;
import com.Social.application.DG2.entity.SharesPosts;

import java.util.List;

public interface SharesPostService {

    SharesPosts createdSharePost(SharesPostDto sharesPosts);

    void deleteSharedPost(String sharesPostId, String currentUsername);

    List<SharesPosts> getSharedPostByCurrentUser(String currentUsername);

    List<SharesPosts> getSharedPostsByUserId(String userId);

}