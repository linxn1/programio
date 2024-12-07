package com.example.programiocode.pojo.dto;

import lombok.Data;

@Data
public class CodeSubmitResponseDTO {
    private String accuracy;
    private String time;
    private String memory;

    public CodeSubmitResponseDTO(String accuracy, String time, String memory) {
        this.accuracy = accuracy;
        this.time = time;
        this.memory = memory;
    }
}
