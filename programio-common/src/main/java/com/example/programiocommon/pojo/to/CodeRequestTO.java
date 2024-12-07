package com.example.programiocommon.pojo.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class CodeRequestTO implements Serializable {
    private String userId;
    private String language;
    private String code;
    private String userInput;
    private Integer questionId;
}
