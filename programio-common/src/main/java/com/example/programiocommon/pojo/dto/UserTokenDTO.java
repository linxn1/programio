package com.example.programiocommon.pojo.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
@Data
@Entity
@Table(name = "user_token")
public class UserTokenDTO {
    @Id
    private Integer id;
    private Integer userAccount;
    private String permission;
    private String token;
    private Timestamp createdAt;
    private Timestamp expiresAt;

}
