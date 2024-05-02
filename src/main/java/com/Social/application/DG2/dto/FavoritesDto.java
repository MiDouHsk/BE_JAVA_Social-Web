package com.Social.application.DG2.dto;

import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class FavoritesDto {
    private Posts postsID;
    private Users userID;
    private Timestamp createAt;
}
