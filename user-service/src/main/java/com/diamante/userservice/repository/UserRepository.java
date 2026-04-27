package com.diamante.userservice.repository;

import com.diamante.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByFoodPreference(String foodPreference);
    List<User> findByLocation(String location);
    boolean existsByEmail(String email);
}
