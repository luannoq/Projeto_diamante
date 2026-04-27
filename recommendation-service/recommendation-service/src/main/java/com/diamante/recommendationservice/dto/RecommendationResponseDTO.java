package com.diamante.recommendationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponseDTO {
    private Long userId;
    private String userName;
    private List<RestaurantDTO> recommendedRestaurants;
    private String aiSuggestion;  // Texto gerado pelo Spring AI
}
