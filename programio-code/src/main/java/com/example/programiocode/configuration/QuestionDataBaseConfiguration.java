package com.example.programiocode.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.mybatis.spring.SqlSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.example.programiocode.mapper.question") // 自动扫描所有 Mapper 接口
@EnableTransactionManagement
public class QuestionDataBaseConfiguration {

    // 配置主数据源
    @Bean(name = "programIOQuestionDataSource")
    public DataSource questionDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/programio_question?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    // 配置 SqlSessionFactory
    @Bean(name = "programIOQuestionSqlSessionFactory")
    public SqlSessionFactory questionSqlSessionFactory(@Qualifier("programIOQuestionDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 如果需要，还可以设置 MyBatis 配置
//         factoryBean.setConfiguration(myBatisConfiguration);

        // 设置 Mapper 文件的位置
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*.xml"));

        return factoryBean.getObject();
    }

    // 配置 SqlSessionTemplate
    @Bean(name = "programIOQuestionSqlSessionTemplate")
    public SqlSessionTemplate questionSqlSessionTemplate(@Qualifier("programIOQuestionSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // 配置事务管理器
    @Bean(name = "programIOQuestionTransactionManager")
    public PlatformTransactionManager questionTransactionManager(@Qualifier("programIOQuestionDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 扫描 Mapper 接口
    @Bean
    public MapperScannerConfigurer questionMapperScannerConfigurer() {
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.example.programiocode.mapper.question");  // 指定Mapper接口包名
        return scannerConfigurer;
    }
}
