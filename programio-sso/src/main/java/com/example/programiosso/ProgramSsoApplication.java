package com.example.programiosso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.programiocommon.pojo")
public class ProgramSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgramSsoApplication.class, args);
    }

}
