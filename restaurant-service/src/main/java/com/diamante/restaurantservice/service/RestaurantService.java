package com.diamante.restaurantservice.service;

import com.diamante.restaurantservice.dto.RestaurantRequestDTO;
import com.diamante.restaurantservice.dto.RestaurantResponseDTO;
import com.diamante.restaurantservice.exception.RestaurantNotFoundException;
import com.diamante.restaurantservice.model.Restaurant;
import com.diamante.restaurantservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO dto) {
        Restaurant restaurant = Restaurant.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .location(dto.getLocation())
                .priceRange(dto.getPriceRange())
                .rating(dto.getRating())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .description(dto.getDescription())
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurante criado com ID: {}", saved.getId());
        return toResponse(saved);
    }

    // Retry com Exponential Backoff para busca por ID
    @Retryable(
        retryFor = { RuntimeException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public RestaurantResponseDTO getRestaurantById(Long id) {
        log.info("Buscando restaurante com ID: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurante não encontrado com ID: " + id));
        return toResponse(restaurant);
    }

    public List<RestaurantResponseDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filtro por categoria (ex: "italiana", "japonesa")
    public List<RestaurantResponseDTO> getByCategory(String category) {
        return restaurantRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filtro por localização
    public List<RestaurantResponseDTO> getByLocation(String location) {
        return restaurantRepository.findByLocation(location)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filtro por faixa de preço
    public List<RestaurantResponseDTO> getByPriceRange(String priceRange) {
        return restaurantRepository.findByPriceRange(priceRange)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filtro por avaliação mínima
    public List<RestaurantResponseDTO> getByMinRating(Double minRating) {
        return restaurantRepository.findByRatingGreaterThanEqual(minRating)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Filtro combinado — usado pelo RecommendationService
    public List<RestaurantResponseDTO> getByFilters(String category, String location, String priceRange) {
        List<Restaurant> results;

        if (category != null && location != null && priceRange != null) {
            results = restaurantRepository.findByCategoryAndLocationAndPriceRange(category, location, priceRange);
        } else if (category != null && location != null) {
            results = restaurantRepository.findByCategoryAndLocation(category, location);
        } else if (category != null && priceRange != null) {
            results = restaurantRepository.findByCategoryAndPriceRange(category, priceRange);
        } else if (category != null) {
            results = restaurantRepository.findByCategory(category);
        } else if (location != null) {
            results = restaurantRepository.findByLocation(location);
        } else {
            results = restaurantRepository.findAll();
        }

        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RestaurantResponseDTO updateRestaurant(Long id, RestaurantRequestDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurante não encontrado com ID: " + id));

        restaurant.setName(dto.getName());
        restaurant.setCategory(dto.getCategory());
        restaurant.setLocation(dto.getLocation());
        restaurant.setPriceRange(dto.getPriceRange());
        restaurant.setRating(dto.getRating());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhone(dto.getPhone());
        restaurant.setDescription(dto.getDescription());

        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurante atualizado: {}", updated.getId());
        return toResponse(updated);
    }

    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantNotFoundException("Restaurante não encontrado com ID: " + id);
        }
        restaurantRepository.deleteById(id);
        log.info("Restaurante deletado: {}", id);
    }

    private RestaurantResponseDTO toResponse(Restaurant r) {
        return RestaurantResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .category(r.getCategory())
                .location(r.getLocation())
                .priceRange(r.getPriceRange())
                .rating(r.getRating())
                .address(r.getAddress())
                .phone(r.getPhone())
                .description(r.getDescription())
                .build();
    }
}
