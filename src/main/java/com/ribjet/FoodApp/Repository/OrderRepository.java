package com.ribjet.FoodApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.Order;
import com.ribjet.FoodApp.Entity.Restaurant;
import com.ribjet.FoodApp.Entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByCustomer(Customer customer);

	List<Order> findByCustomerOrderByOrderTimeDesc(Customer customer);

	Restaurant findByRestaurant(User user);

	@Query("SELECT o FROM Order o WHERE o.restaurant = :restaurant ORDER BY CASE WHEN o.status = 'DELIVERED' THEN 1 ELSE 0  END, o.orderTime DESC")
	List<Order> findByRestaurant(@Param("restaurant") Restaurant restaurant);

	Order findTopByCustomerOrderByOrderTimeDesc(Customer customer);
}
