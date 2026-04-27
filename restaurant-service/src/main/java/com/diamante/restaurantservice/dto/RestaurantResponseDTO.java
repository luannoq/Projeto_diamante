package com.diamante.restaurantservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String location;
    private String priceRange;
    private Double rating;
    private String address;
    private String phone;
    private String description;
}
