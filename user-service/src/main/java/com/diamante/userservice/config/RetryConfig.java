package com.diamante.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    /**
     * RetryTemplate com Exponential Backoff configurado manualmente.
     * Pode ser injetado em outros serviços para chamadas a APIs externas.
     *
     * Backoff: 1s -> 2s -> 4s (multiplier = 2.0)
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Exponential Backoff
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);    // 1 segundo inicial
        backOffPolicy.setMultiplier(2.0);           // dobra a cada tentativa
        backOffPolicy.setMaxInterval(10000);        // máximo 10 segundos

        // Máximo de tentativas
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
