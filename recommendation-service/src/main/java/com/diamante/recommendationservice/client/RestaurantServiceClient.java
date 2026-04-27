package com.diamante.recommendationservice.client;

import com.diamante.recommendationservice.dto.RestaurantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceClient {

    private final RestTemplate restTemplate;

    private static final String RESTAURANT_SERVICE_URL = "http://restaurant-service/api/restaurants";

    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public List<RestaurantDTO> getRestaurantsByFilters(String category, String location, String priceRange) {
        log.info("Chamando RestaurantService - categoria: {}, local: {}, preço: {}", category, location, priceRange);

        String url = UriComponentsBuilder.fromHttpUrl(RESTAURANT_SERVICE_URL + "/filter")
                .queryParam("category", category)
                .queryParam("location", location)
                .queryParam("priceRange", priceRange)
                .toUriString();

        RestaurantDTO[] result = restTemplate.getForObject(url, RestaurantDTO[].class);
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}
