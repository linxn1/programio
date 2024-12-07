package com.example.programiocode.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {
    @MapperScan(basePackages = "com.example.programiocode.mapper.question", sqlSessionTemplateRef = "programIOQuestionSqlSessionTemplate")
    public class QuestionMapperConfig {
        // 只扫描 Question 相关的 Mapper 并使用指定的 SqlSessionTemplate
    }


    @MapperScan(basePackages = "com.example.programiocode.mapper.submit", sqlSessionTemplateRef = "programIOSubmitSqlSessionTemplate")
    public class SubmitMapperConfig {
        // 只扫描 Submit 相关的 Mapper 并使用指定的 SqlSessionTemplate
    }


    @MapperScan(basePackages = "com.example.programiocode.mapper.user", sqlSessionTemplateRef = "programIOUserSqlSessionTemplate")
    public class UserMapperConfig {
        // 只扫描 User 相关的 Mapper 并使用指定的 SqlSessionTemplate
    }

}
