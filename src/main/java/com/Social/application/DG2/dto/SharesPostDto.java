package com.Social.application.DG2.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SharesPostDto {

    private String postDtoId;

    private String userDtoId;

    private Timestamp createAt;

}
