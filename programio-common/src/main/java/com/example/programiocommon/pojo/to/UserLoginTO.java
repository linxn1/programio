package com.example.programiocommon.pojo.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginTO implements Serializable {
    private Integer userAccount;
    private String password;
}
