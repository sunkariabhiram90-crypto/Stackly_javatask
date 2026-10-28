package com.example.foodorder.dto;

import com.example.foodorder.entity.FoodOrder;
import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        String customerName,
        String foodItem,
        Integer quantity,
        BigDecimal price) {
    public static OrderResponse from(FoodOrder order) {
        return new OrderResponse(order.getOrderId(), order.getCustomerName(), order.getFoodItem(),
                order.getQuantity(), order.getPrice());
    }
}
