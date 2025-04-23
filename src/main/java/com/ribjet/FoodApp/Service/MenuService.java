package com.ribjet.FoodApp.Service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ribjet.FoodApp.Entity.Menu;
import com.ribjet.FoodApp.Repository.MenuRepository;


@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	public void save(Menu menu) {

		menu.setCreatedAt(new Date());
		menuRepository.save(menu);
	}
	
	public Menu getMenuItemById(Integer itemId) {
		return menuRepository.getById(itemId);
	}
	public List<Menu> getAllItems(Integer restaurantId) {
		return menuRepository.findByRestaurant_RestaurantId(restaurantId);
			}
}
