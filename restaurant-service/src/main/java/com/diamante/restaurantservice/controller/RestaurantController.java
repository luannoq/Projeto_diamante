package com.diamante.restaurantservice.controller;

import com.diamante.restaurantservice.dto.RestaurantRequestDTO;
import com.diamante.restaurantservice.dto.RestaurantResponseDTO;
import com.diamante.restaurantservice.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(
            @Valid @RequestBody RestaurantRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    // Filtro por categoria
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RestaurantResponseDTO>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(restaurantService.getByCategory(category));
    }

    // Filtro por localização
    @GetMapping("/location/{location}")
    public ResponseEntity<List<RestaurantResponseDTO>> getByLocation(
            @PathVariable String location) {
        return ResponseEntity.ok(restaurantService.getByLocation(location));
    }

    // Filtro por faixa de preço
    @GetMapping("/price/{priceRange}")
    public ResponseEntity<List<RestaurantResponseDTO>> getByPriceRange(
            @PathVariable String priceRange) {
        return ResponseEntity.ok(restaurantService.getByPriceRange(priceRange));
    }

    // Filtro por avaliação mínima
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<RestaurantResponseDTO>> getByMinRating(
            @PathVariable Double minRating) {
        return ResponseEntity.ok(restaurantService.getByMinRating(minRating));
    }

    // Filtro combinado — endpoint principal para o RecommendationService
    @GetMapping("/filter")
    public ResponseEntity<List<RestaurantResponseDTO>> getByFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String priceRange) {
        return ResponseEntity.ok(restaurantService.getByFilters(category, location, priceRange));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDTO dto) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
