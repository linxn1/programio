package com.example.programiocommon.pojo.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class CodeRespondTO implements Serializable {
    private String isCorrect;
    private String time;
    private String memory;
    private String codeOutput;
    private String errorOutput;
    private String language;

    private String isAccepted;
    private String accuracyRate;

    @Override
    public String toString() {
        return "CodeRespondTO{" +
                "isCorrect='" + isCorrect + '\'' +
                ", time='" + time + '\'' +
                ", memory='" + memory + '\'' +
                ", codeOutput='" + codeOutput + '\'' +
                ", errorOutput='" + errorOutput + '\'' +
                ", language='" + language + '\'' +
                ", isAccepted='" + isAccepted + '\'' +
                ", accuracyRate=" + accuracyRate +
                '}';
    }
}
