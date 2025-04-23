package com.ribjet.FoodApp.Entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "restaurant")
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer restaurantId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "user_Id")
    private User owner;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String address;

    private double rating;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "opening_hours")
    private String openingHours;
    
    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus;
    
	public Restaurant() {
	}

	public Restaurant(Integer restaurantId, User owner, String name, String address, double rating, String cuisineType,
			String contactNumber, String openingHours, Date createdAt,List<Menu> menus) {
		this.restaurantId = restaurantId;
		this.owner = owner;
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.cuisineType = cuisineType;
		this.contactNumber = contactNumber;
		this.openingHours = openingHours;
		this.createdAt = createdAt;
		this.menus=menus;
		
	}

	public Restaurant(User savedUser) {
		this.owner=savedUser;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getCuisineType() {
		return cuisineType;
	}

	public void setCuisineType(String cuisineType) {
		this.cuisineType = cuisineType;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
	
	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	
	
	

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Restaurant [restaurantId=" + restaurantId + ", owner=" + owner + ", name=" + name + ", address="
				+ address + ", rating=" + rating + ", cuisineType=" + cuisineType + ", contactNumber=" + contactNumber
				+ ", openingHours=" + openingHours + ", createdAt=" + createdAt + "]";
	}
    
	
    
}
