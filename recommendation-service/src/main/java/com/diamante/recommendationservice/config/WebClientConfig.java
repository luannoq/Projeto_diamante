package com.diamante.recommendationservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfig {

    /**
     * RestTemplate com @LoadBalanced permite usar o nome do serviço
     * registrado no Eureka ao invés de IP/porta fixos.
     * O Spring Cloud LoadBalancer faz o balanceamento automaticamente.
     *
     * Uso: restTemplate.getForObject("http://user-service/api/users/1", UserDTO.class)
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
