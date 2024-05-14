package com.Social.application.DG2.dto;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class SearchUserDto {
    //chỉ in những trường này khi tìm kiếm ----
    private String id;
    private String username;
    private String mail;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean gender;
    private Timestamp dateOfBirth;
    private String address;
    private String avatar;
    private String background;
    private Timestamp createAt;


}
