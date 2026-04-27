package com.diamante.recommendationservice.service;

import com.diamante.recommendationservice.client.RestaurantServiceClient;
import com.diamante.recommendationservice.client.UserServiceClient;
import com.diamante.recommendationservice.dto.*;
import com.diamante.recommendationservice.exception.RecommendationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RecommendationService {

    private final UserServiceClient userServiceClient;
    private final RestaurantServiceClient restaurantServiceClient;
    private final RestTemplate loadBalancedRestTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public RecommendationService(UserServiceClient userServiceClient,
                                  RestaurantServiceClient restaurantServiceClient,
                                  RestTemplate loadBalancedRestTemplate) {
        this.userServiceClient = userServiceClient;
        this.restaurantServiceClient = restaurantServiceClient;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    public RecommendationResponseDTO getRecommendations(RecommendationRequestDTO request) {
        log.info("Buscando dados do usuário: {}", request.getUserId());
        UserDTO user = userServiceClient.getUserById(request.getUserId());

        if (user == null) {
            throw new RecommendationException("Usuário não encontrado: " + request.getUserId());
        }

        String category   = request.getOverrideCategory()   != null ? request.getOverrideCategory()   : user.getFoodPreference();
        String location   = request.getOverrideLocation()   != null ? request.getOverrideLocation()   : user.getLocation();
        String priceRange = request.getOverridePriceRange() != null ? request.getOverridePriceRange() : user.getPriceRange();

        log.info("Buscando restaurantes - categoria: {}, local: {}, preço: {}", category, location, priceRange);
        List<RestaurantDTO> restaurants = restaurantServiceClient.getRestaurantsByFilters(category, location, priceRange);

        String aiSuggestion = generateAiSuggestion(user, restaurants);

        return RecommendationResponseDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .recommendedRestaurants(restaurants)
                .aiSuggestion(aiSuggestion)
                .build();
    }

    private String generateAiSuggestion(UserDTO user, List<RestaurantDTO> restaurants) {
        if (restaurants.isEmpty()) {
            return "Não encontramos restaurantes com essas preferências. Tente ampliar os filtros!";
        }

        StringBuilder restaurantList = new StringBuilder();
        restaurants.forEach(r -> restaurantList
                .append("- ").append(r.getName())
                .append(" (").append(r.getCategory()).append(")")
                .append(", em ").append(r.getLocation())
                .append(", preço: ").append(r.getPriceRange())
                .append(", avaliação: ").append(r.getRating()).append("/5\n"));

        String prompt = String.format("""
                Você é um assistente de recomendação de restaurantes.
                
                Perfil do usuário:
                - Nome: %s
                - Preferência: %s
                - Localização: %s
                - Faixa de preço: %s
                
                Restaurantes disponíveis:
                %s
                
                Recomende de forma personalizada e amigável quais restaurantes visitar e por quê.
                Máximo 3 parágrafos.
                """,
                user.getName(), user.getFoodPreference(),
                user.getLocation(), user.getPriceRange(),
                restaurantList);

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Monta o body no formato da API do Gemini
            Map<String, Object> body = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                GEMINI_URL + geminiApiKey, entity, Map.class);

            // Extrai o texto da resposta do Gemini
            var candidates = (List<?>) response.getBody().get("candidates");
            var content = (Map<?, ?>) ((Map<?, ?>) candidates.get(0)).get("content");
            var parts = (List<?>) content.get("parts");
            return (String) ((Map<?, ?>) parts.get(0)).get("text");

        } catch (Exception e) {
            log.error("Erro ao chamar Gemini: {}", e.getMessage());
            return "Sugestão IA indisponível no momento. Confira os restaurantes listados!";
        }
    }
}
