package com.diamante.recommendationservice.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
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
