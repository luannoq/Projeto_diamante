package com.diamante.restaurantservice.repository;

import com.diamante.restaurantservice.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCategory(String category);
    List<Restaurant> findByLocation(String location);
    List<Restaurant> findByPriceRange(String priceRange);
    List<Restaurant> findByRatingGreaterThanEqual(Double rating);
    List<Restaurant> findByCategoryAndLocation(String category, String location);
    List<Restaurant> findByCategoryAndPriceRange(String category, String priceRange);
    List<Restaurant> findByCategoryAndLocationAndPriceRange(String category, String location, String priceRange);
}
