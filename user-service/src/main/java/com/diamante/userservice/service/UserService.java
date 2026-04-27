package com.diamante.userservice.service;

import com.diamante.userservice.dto.UserRequestDTO;
import com.diamante.userservice.dto.UserResponseDTO;
import com.diamante.userservice.exception.UserAlreadyExistsException;
import com.diamante.userservice.exception.UserNotFoundException;
import com.diamante.userservice.model.User;
import com.diamante.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email já cadastrado: " + dto.getEmail());
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .foodPreference(dto.getFoodPreference())
                .priceRange(dto.getPriceRange())
                .location(dto.getLocation())
                .build();

        User saved = userRepository.save(user);
        log.info("Usuário criado com ID: {}", saved.getId());
        return toResponse(saved);
    }

    // Demonstração de Spring Retry com Exponential Backoff
    @Retryable(
        retryFor = { RuntimeException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0)  // Exponential Backoff
    )
    public UserResponseDTO getUserById(Long id) {
        log.info("Buscando usuário com ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));
        return toResponse(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com email: " + email));
        return toResponse(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setFoodPreference(dto.getFoodPreference());
        user.setPriceRange(dto.getPriceRange());
        user.setLocation(dto.getLocation());

        User updated = userRepository.save(user);
        log.info("Usuário atualizado: {}", updated.getId());
        return toResponse(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("Usuário deletado: {}", id);
    }

    public List<UserResponseDTO> getUsersByFoodPreference(String foodPreference) {
        return userRepository.findByFoodPreference(foodPreference)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .foodPreference(user.getFoodPreference())
                .priceRange(user.getPriceRange())
                .location(user.getLocation())
                .build();
    }
}
