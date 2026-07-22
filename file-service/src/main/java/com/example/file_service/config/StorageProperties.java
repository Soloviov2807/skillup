package com.example.file_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "app.image-storage")
public record StorageProperties(
    String basePath,
    Set<String> allowedMimeTypes
) {}
