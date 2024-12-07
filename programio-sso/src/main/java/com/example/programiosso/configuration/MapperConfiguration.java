package com.example.programiosso.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.programsso.mapper.user")
public class MapperConfiguration {

    @MapperScan(basePackages = "com.example.programiosso.mapper.user", sqlSessionTemplateRef = "programIOUserSqlSessionTemplate")
    public class UserMapperConfig {
        // 只扫描 User 相关的 Mapper 并使用指定的 SqlSessionTemplate
    }

}
