package com.ribjet.FoodApp.Service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribjet.FoodApp.Entity.Restaurant;
import com.ribjet.FoodApp.Entity.User;
import com.ribjet.FoodApp.Repository.OrderRepository;
import com.ribjet.FoodApp.Repository.RestaurantRepository;

@Service
public class RestaurantService {
	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private OrderRepository orderRepository;
	
	public List<Restaurant> getRestaurantsByOwnerId(Integer userId) {
		return restaurantRepository.findByOwnerId(userId);
	}

	public List<Restaurant> getAll() {
		return restaurantRepository.findAll();
	}

	public Restaurant getRestaurantById(Integer restaurantId) {
		return restaurantRepository.getById(restaurantId);
	}

	public void editRestaurant(Restaurant restaurant) {
		restaurant.setCreatedAt(new Date());
		System.out.println("Created at: "+restaurant.getCreatedAt());
		restaurantRepository.save(restaurant);
		
	}

	public Restaurant getRestaurantByOwner(User user) {
	    return restaurantRepository.findByOwner(user);
	}
}
