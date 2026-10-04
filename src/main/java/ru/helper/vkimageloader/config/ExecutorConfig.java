package ru.helper.vkimageloader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    
    @Value("${spring.application.executor-threads: 2}")
    private int threads;

    @Bean
    public ExecutorService photoDownloadExecutor() {
        return Executors.newFixedThreadPool(threads);
    }
}