package com.diamante.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Preferências do usuário conforme pedido no PDF
    private String foodPreference;   // ex: "italiana", "japonesa", "vegana"
    private String priceRange;       // ex: "barato", "médio", "caro"
    private String location;         // bairro ou cidade
}
