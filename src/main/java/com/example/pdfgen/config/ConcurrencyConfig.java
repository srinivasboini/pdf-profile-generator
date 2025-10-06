package com.example.pdfgen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for concurrent request handling and async operations
 * Ensures thread-safe PDF generation for multiple concurrent users
 */
@Configuration
@Slf4j
public class ConcurrencyConfig {

    /**
     * Configure async executor for potential async PDF generation tasks
     * This is optional but provides better control over concurrent operations
     */
    @Bean(name = "pdfTaskExecutor")
    public Executor pdfTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - minimum number of threads
        executor.setCorePoolSize(10);

        // Max pool size - maximum number of threads
        executor.setMaxPoolSize(50);

        // Queue capacity - requests waiting when all threads are busy
        executor.setQueueCapacity(100);

        // Thread name prefix for debugging
        executor.setThreadNamePrefix("pdf-async-");

        // Rejection policy when queue is full
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        log.info("PDF Task Executor initialized: core={}, max={}, queue={}",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getQueueCapacity());

        return executor;
    }
}
