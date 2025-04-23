package com.ribjet.FoodApp.Entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_Id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurantId", nullable = false)
    private Restaurant restaurant;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_time", nullable = false, updatable = false)
    private Date orderTime;

    @Column(nullable = false)
    private String status; // Example: "Pending", "Confirmed", "Delivered"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private double totalPrice;
    
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    // New fields for Razorpay integration
    @Column(name = "razorpay_order_id", unique = true)
    private String razorpayOrderId; // Stores Razorpay's generated order ID

    @Column(name = "payment_id", unique = true)
    private String paymentId; // Stores payment ID after successful payment

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false; // Indicates whether payment is completed
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "COD"; // Default to COD

    public Order() {
        this.orderTime = new Date();
        this.status = "Pending";
    }
}
