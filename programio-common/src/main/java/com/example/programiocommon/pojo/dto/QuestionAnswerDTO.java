package com.example.programiocommon.pojo.dto;



import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "question_answer")
public class QuestionAnswerDTO {
    @Id
    private Integer id;

    private Integer questionId;
    private String inputCase;

    private String answer;

    // 无参构造函数（JPA要求通常需要）
    public QuestionAnswerDTO() {
    }

}
