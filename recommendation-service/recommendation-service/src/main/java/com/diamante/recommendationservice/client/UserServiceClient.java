package com.diamante.recommendationservice.client;

import com.diamante.recommendationservice.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    // Nome do serviço registrado no Eureka — LoadBalancer resolve o IP automaticamente
    private static final String USER_SERVICE_URL = "http://user-service/api/users";

    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public UserDTO getUserById(Long userId) {
        log.info("Chamando UserService para userId: {}", userId);
        return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, UserDTO.class);
    }
}
