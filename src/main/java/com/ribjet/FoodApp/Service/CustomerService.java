package com.ribjet.FoodApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.User;
import com.ribjet.FoodApp.Repository.CustomerRepository;


@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Customer findByUserId(Integer userId) {
	    return customerRepository.findByUserId(userId);
	}

	public Customer findByOwner(User user) {
		
		return customerRepository.findByOwner(user);
	}

	public void save(Customer customer) {
		customerRepository.save(customer);
		
	}
}
