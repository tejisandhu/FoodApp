package com.ribjet.FoodApp.Service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.Restaurant;
import com.ribjet.FoodApp.Entity.User;
import com.ribjet.FoodApp.Repository.CustomerRepository;
import com.ribjet.FoodApp.Repository.RestaurantRepository;
import com.ribjet.FoodApp.Repository.UserRepository;


@Service
public class UserService {

	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private CustomerRepository customerRepository;
	
	public Optional<User> findByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	public User addNew(User user) {
		user.setRegistrationDate(new Date());

		User savedUser = userRepository.save(user);

		int userType = savedUser.getUserType().getUserTypeId();
		if (userType == 2) {
			restaurantRepository.save(new Restaurant(savedUser));
		} else if (userType == 1) {
			customerRepository.save(new Customer(savedUser));
		}
		return savedUser;		
	}

	public void save(User userData) {
		userRepository.save(userData);
	}
}
