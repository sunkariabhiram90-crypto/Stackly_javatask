package com.example.foodorder.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "food_orders")
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 120)
    private String foodItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected FoodOrder() {
    }

    public FoodOrder(String customerName, String foodItem, Integer quantity, BigDecimal price) {
        this.customerName = customerName;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getFoodItem() { return foodItem; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setFoodItem(String foodItem) { this.foodItem = foodItem; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
