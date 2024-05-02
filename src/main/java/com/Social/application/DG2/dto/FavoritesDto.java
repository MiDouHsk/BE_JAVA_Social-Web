package com.Social.application.DG2.dto;

import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class FavoritesDto {
    private String postsID;
    private String title;
    private String body;
    private String status;
    private String totalLike;
    private List<String> mediasId;
    private String totalComment;
    private String userID;
    private Timestamp createAt;
}
