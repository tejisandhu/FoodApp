package com.ribjet.FoodApp.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="contactus")
public class Contactus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	private String message;
	public Contactus() {
	
	}
	public Contactus(Integer id, String name, String message) {
		this.id = id;
		this.name = name;
		this.message = message;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Contactus [id=" + id + ", name=" + name + ", message=" + message + "]";
	}
	
	
	
	
}
