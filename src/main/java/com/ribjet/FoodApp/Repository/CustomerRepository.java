package com.ribjet.FoodApp.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.User;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Customer findByOwner(User user);

	@Query("SELECT c FROM Customer c WHERE c.owner.id = :userId")
	Customer findByUserId(@Param("userId") Integer userId);

}
