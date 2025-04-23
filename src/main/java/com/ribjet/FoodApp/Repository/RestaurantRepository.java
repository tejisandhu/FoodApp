package com.ribjet.FoodApp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ribjet.FoodApp.Entity.Restaurant;
import com.ribjet.FoodApp.Entity.User;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>{

	


	List<Restaurant> findByOwnerId(int ownerId);
	
//	@Query("SELECT r FROM Restaurant r WHERE r.owner.id = :ownerId")
//	List<Restaurant> findByOwnerId(@Param("ownerId") Integer ownerId);
	Restaurant findByOwner(User owner);


}
