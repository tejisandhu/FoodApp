package com.ribjet.FoodApp.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_id", referencedColumnName = "itemId", nullable = false)
    private Menu menuItem;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price; // Price per unit

    public double getTotalPrice() {
        return this.price * this.quantity;
    }
}
