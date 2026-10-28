package com.example.foodorder.service;

import com.example.foodorder.dto.OrderRequest;
import com.example.foodorder.dto.OrderResponse;
import com.example.foodorder.entity.FoodOrder;
import com.example.foodorder.exception.OrderNotFoundException;
import com.example.foodorder.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {
    private static final Set<String> PATCH_FIELDS = Set.of("customerName", "foodItem", "quantity", "price");
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return repository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return OrderResponse.from(findOrder(id));
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        FoodOrder order = new FoodOrder(normalize(request.getCustomerName()), normalize(request.getFoodItem()),
                request.getQuantity(), request.getPrice());
        return OrderResponse.from(repository.save(order));
    }

    @Transactional
    public OrderResponse replace(Long id, OrderRequest request) {
        FoodOrder order = findOrder(id);
        order.setCustomerName(normalize(request.getCustomerName()));
        order.setFoodItem(normalize(request.getFoodItem()));
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        return OrderResponse.from(repository.save(order));
    }

    @Transactional
    public OrderResponse patch(Long id, JsonNode patch) {
        FoodOrder order = findOrder(id);
        if (patch == null || !patch.isObject()) {
            throw new IllegalArgumentException("PATCH body must be a JSON object");
        }
        if (patch.isEmpty()) {
            throw new IllegalArgumentException("PATCH body must contain at least one field");
        }

        Iterator<String> names = patch.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!PATCH_FIELDS.contains(name)) {
                throw new IllegalArgumentException("Unknown or non-editable field: " + name);
            }
            JsonNode value = patch.get(name);
            if (value == null || value.isNull()) {
                throw new IllegalArgumentException(name + " cannot be null");
            }
            switch (name) {
                case "customerName" -> {
                    if (!value.isTextual()) throw new IllegalArgumentException("customerName must be a string");
                    String text = normalize(value.asText());
                    if (text.isBlank()) throw new IllegalArgumentException("Customer name is required");
                    if (text.length() > 100) throw new IllegalArgumentException("Customer name must not exceed 100 characters");
                    order.setCustomerName(text);
                }
                case "foodItem" -> {
                    if (!value.isTextual()) throw new IllegalArgumentException("foodItem must be a string");
                    String text = normalize(value.asText());
                    if (text.isBlank()) throw new IllegalArgumentException("Food item cannot be empty");
                    if (text.length() > 120) throw new IllegalArgumentException("Food item must not exceed 120 characters");
                    order.setFoodItem(text);
                }
                case "quantity" -> {
                    if (!value.isIntegralNumber() || !value.canConvertToInt())
                        throw new IllegalArgumentException("Quantity must be an integer");
                    if (value.intValue() < 1) throw new IllegalArgumentException("Quantity must be at least 1");
                    order.setQuantity(value.intValue());
                }
                case "price" -> {
                    if (!value.isNumber()) throw new IllegalArgumentException("Price must be a number");
                    BigDecimal price = value.decimalValue();
                    if (price.compareTo(BigDecimal.ZERO) <= 0)
                        throw new IllegalArgumentException("Price must be greater than zero");
                    if (price.scale() > 2 || price.precision() - price.scale() > 8)
                        throw new IllegalArgumentException("Price must have at most 8 integer digits and 2 decimal places");
                    order.setPrice(price);
                }
                default -> throw new IllegalArgumentException("Unknown field");
            }
        }
        return OrderResponse.from(repository.save(order));
    }

    @Transactional
    public void delete(Long id) {
        FoodOrder order = findOrder(id);
        repository.delete(order);
    }

    private FoodOrder findOrder(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Order ID must be a positive integer");
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
