package com.rainydays_engine.rainydays.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sh.ory.kratos.ApiClient;
import sh.ory.kratos.api.FrontendApi;
import sh.ory.kratos.api.IdentityApi;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class KratosConfig {

    @Value("${kratos.public-url}")
    private String KRATOS_PUBLIC_URL;

    @Value("${kratos.private-url}")
    private String KRATOS_PRIVATE_URL; // Admin

    @Bean
    public ApiClient kratosPublicApiClient() {
        ApiClient client = sh.ory.kratos.Configuration.getDefaultApiClient();
        client.setBasePath(KRATOS_PUBLIC_URL);
        return client;
    }

    @Bean
    public ApiClient kratosPrivateApiClient() {
        ApiClient client = sh.ory.kratos.Configuration.getDefaultApiClient();
        client.setBasePath(KRATOS_PRIVATE_URL);
        return client;
    }

    @Bean
    public FrontendApi frontendApi(ApiClient kratosPublicApiClient) {
        return new FrontendApi(kratosPublicApiClient);
    }

    @Bean
    public IdentityApi adminApi(ApiClient kratosPrivateApiClient) {
        return new IdentityApi(kratosPrivateApiClient);
    }
}
