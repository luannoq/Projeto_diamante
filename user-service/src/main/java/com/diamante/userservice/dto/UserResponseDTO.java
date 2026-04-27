package com.diamante.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String foodPreference;
    private String priceRange;
    private String location;
}
