package com.ribjet.FoodApp.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribjet.FoodApp.Entity.Customer;
import com.ribjet.FoodApp.Entity.Order;
import com.ribjet.FoodApp.Entity.Restaurant;
import com.ribjet.FoodApp.Repository.MenuRepository;
import com.ribjet.FoodApp.Repository.OrderItemRepository;
import com.ribjet.FoodApp.Repository.OrderRepository;
import com.ribjet.FoodApp.Entity.Menu;
import com.ribjet.FoodApp.Entity.OrderItem;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	 @Transactional
	    public Order placeOrder(Customer customer, Restaurant restaurant, Map<Integer, Integer> orderItems, String deliveryAddress) {
	        // Create new order
	        Order order = new Order();
	        order.setCustomer(customer);
	        order.setRestaurant(restaurant);
	        order.setOrderTime(new Date());
	        order.setStatus("PENDING");
	        order.setDeliveryAddress(deliveryAddress); // Save delivery address

	        double totalPrice = 0.0;

	        // Save order first to generate an ID
	        order = orderRepository.save(order);

	        // Save order items
	        for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
	            Integer menuId = entry.getKey();
	            Integer quantity = entry.getValue();

	            Menu menuItem = menuRepository.findById(menuId).orElse(null);
	            if (menuItem != null) {
	                OrderItem orderItem = new OrderItem();
	                orderItem.setOrder(order);
	                orderItem.setMenuItem(menuItem);
	                orderItem.setQuantity(quantity);
	                orderItem.setPrice(menuItem.getPrice() * quantity);
	                orderItemRepository.save(orderItem);

	                totalPrice += menuItem.getPrice() * quantity;
	            }
	        }

	        // Update total price in order
	        order.setTotalPrice(totalPrice);
	       return orderRepository.save(order);
	    }

	 public Order getOrderById(int orderId) {
			
			return orderRepository.getById(orderId);
		}

	public List<Order> getPastOrders(Customer customer) {
		return orderRepository.findByCustomerOrderByOrderTimeDesc(customer);
	}
	
	public void updateOrderStatus(int orderId, String newStatus) {
		 Order order = orderRepository.findById(orderId).orElse(null);
		 String oldStatus = order.getStatus();
	        order.setStatus(newStatus);
	        orderRepository.save(order);

	        // Print status change in console
	        System.out.println("Order ID: " + orderId + " - Status changed from " + oldStatus + " to " + newStatus);
	   
	        }

	 public List<Order> getOrdersForRestaurant(Restaurant restaurant) {    	
        return orderRepository.findByRestaurant(restaurant); 
    }

}
