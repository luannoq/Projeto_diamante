package com.diamante.recommendationservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String foodPreference;
    private String priceRange;
    private String location;
}
