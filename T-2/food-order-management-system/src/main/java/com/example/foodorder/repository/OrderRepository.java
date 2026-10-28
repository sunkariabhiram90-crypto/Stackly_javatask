package com.example.foodorder.repository;

import com.example.foodorder.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
}
