package com.talharic.referral.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String username;

    private String name;
    private String surname;
    private String mail;
    private String referralCode;
    private String referredByCode;

}
