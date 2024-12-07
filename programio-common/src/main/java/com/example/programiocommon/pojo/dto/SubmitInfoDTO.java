package com.example.programiocommon.pojo.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "submit_info")
public class SubmitInfoDTO {
    @Id
    private Integer id;
    private Integer userAccount;
    private String questionId;
    private String languageType;
    private String code;
    private Float accuracy;
    private Float runningTime;
}
