package com.diamante.recommendationservice.controller;

import com.diamante.recommendationservice.dto.RecommendationRequestDTO;
import com.diamante.recommendationservice.dto.RecommendationResponseDTO;
import com.diamante.recommendationservice.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Endpoint principal: recebe o userId, busca preferências no UserService,
     * busca restaurantes no RestaurantService e retorna sugestão com IA.
     *
     * POST /api/recommendations
     * Body: { "userId": 1 }
     */
    @PostMapping
    public ResponseEntity<RecommendationResponseDTO> getRecommendations(
            @Valid @RequestBody RecommendationRequestDTO request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }

    /**
     * Atalho via GET para facilitar testes rápidos.
     * GET /api/recommendations/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendationsByUserId(
            @PathVariable Long userId) {
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setUserId(userId);
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }
}
