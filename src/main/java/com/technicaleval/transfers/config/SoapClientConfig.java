package com.technicaleval.transfers.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soap.transfers")
public record SoapClientConfig(
        String endpoint,
        String password,
        Integer connectTimeoutMs,
        Integer readTimeoutMs
) {
}
