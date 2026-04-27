package com.diamante.restaurantservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RestaurantRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @NotBlank(message = "Localização é obrigatória")
    private String location;

    private String priceRange;

    @DecimalMin(value = "0.0", message = "Avaliação mínima é 0")
    @DecimalMax(value = "5.0", message = "Avaliação máxima é 5")
    private Double rating;

    private String address;
    private String phone;
    private String description;
}
