package com.ribjet.FoodApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ribjet.FoodApp.Entity.Contactus;

@Repository
public interface ContactusRepository extends JpaRepository<Contactus, Integer>{

}
