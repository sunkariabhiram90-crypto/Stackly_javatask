package com.example.foodorder.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found with ID: " + id);
    }
}
