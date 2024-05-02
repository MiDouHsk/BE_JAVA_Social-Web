package com.Social.application.DG2.dto;

import com.Social.application.DG2.entity.Posts;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CommentsDto {
    private String id;

    private String content;

    private int totalLike;

    private Timestamp createAt;

    private String postId;

    private String createBy;

}
