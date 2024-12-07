package com.example.programiocode.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfiguration {

    @Bean(name = "testTaskExecutor")
    public ThreadPoolTaskExecutor testTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);  // 设置核心线程数
        executor.setMaxPoolSize(100);  // 设置最大线程数
        executor.setQueueCapacity(10000); // 设置队列容量
        executor.setThreadNamePrefix("test-task-"); // 设置线程名前缀
        executor.initialize();
        return executor;
    }

    @Bean(name = "submitTaskExecutor")
    public ThreadPoolTaskExecutor submitTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);  // 设置核心线程数
        executor.setMaxPoolSize(100);  // 设置最大线程数
        executor.setQueueCapacity(10000); // 设置队列容量
        executor.setThreadNamePrefix("submit-task-"); // 设置线程名前缀
        executor.initialize();
        return executor;
    }
}
