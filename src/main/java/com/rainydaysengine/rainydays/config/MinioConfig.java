package com.rainydaysengine.rainydays.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String MINIO_ENDPOINT;

    @Value("${minio.user}")
    private String MINIO_ACCESS_KEY;

    @Value("${minio.password}")
    private String MINIO_SECRET_KEY;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(MINIO_ENDPOINT)
                .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                .build();
    }
}
