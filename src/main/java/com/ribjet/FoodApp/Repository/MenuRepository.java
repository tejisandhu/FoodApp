package com.ribjet.FoodApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ribjet.FoodApp.Entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer>{

	List<Menu> findByRestaurant_RestaurantId(Integer restaurantId2); 
}
