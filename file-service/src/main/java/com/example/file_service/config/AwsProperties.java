package com.example.file_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws")
public record AwsProperties(
        String region,
        String accessKey,
        String secretKey,
        String bucket
) {
}
