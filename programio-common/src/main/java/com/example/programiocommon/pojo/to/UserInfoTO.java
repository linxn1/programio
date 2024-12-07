package com.example.programiocommon.pojo.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoTO implements Serializable {
    private Integer userAccount;
    private String userName;
    private String email;
    private String password;
}

