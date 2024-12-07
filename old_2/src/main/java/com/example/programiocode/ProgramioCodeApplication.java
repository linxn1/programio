package com.example.programiocode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.programiocommon.pojo")
public class ProgramioCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgramioCodeApplication.class, args);
    }

}
