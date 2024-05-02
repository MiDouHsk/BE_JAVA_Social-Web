package com.Social.application.DG2.dto;


import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersDto {
    private String id;
    private String username;
    private String password;
    private String mail;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean gender;
    private Timestamp dateOfBirth;
    private String avatar;
//    private boolean enable;
//    private Timestamp createAt;
//    private Timestamp updateAt;
    private String address;

}