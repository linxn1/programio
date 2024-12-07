package com.example.programiocommon.pojo.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "question_info")
public class QuestionInfoDTO {
    @Id
    private Integer id;
    private Integer questionId;
    private String difficulty;
    private String question;
    private Integer timeRequest;
    private Integer memoryRequest;
}
