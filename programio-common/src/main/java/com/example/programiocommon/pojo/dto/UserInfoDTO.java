package com.example.programiocommon.pojo.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user_info")
public class UserInfoDTO { //注册的时候直接用这个,就不写TO了
    @Id
    private Integer id;
    private Integer userAccount;
    private String userName;
    private String email;
    private String password;
    private String permission;

}
