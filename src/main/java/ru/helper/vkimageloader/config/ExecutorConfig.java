package ru.helper.vkimageloader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService photoDownloadExecutor() {
        return Executors.newFixedThreadPool(15);
    }
}