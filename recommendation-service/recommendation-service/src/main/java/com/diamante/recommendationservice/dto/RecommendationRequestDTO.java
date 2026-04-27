package com.diamante.recommendationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecommendationRequestDTO {

    @NotNull(message = "userId é obrigatório")
    private Long userId;

    // Opcional: sobrescreve as preferências do usuário para personalização extra
    private String overrideCategory;
    private String overrideLocation;
    private String overridePriceRange;
}
