package com.example.programiocommon.pojo.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionRequestTO implements Serializable {
    private String difficulty;
    private String question;
    private Integer timeRequest;
    private Integer memoryRequest;
}
