package com.example.foodorder.controller;

import com.example.foodorder.dto.OrderRequest;
import com.example.foodorder.dto.OrderResponse;
import com.example.foodorder.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.orderId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public OrderResponse replace(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return service.replace(id, request);
    }

    @PatchMapping("/{id}")
    public OrderResponse patch(@PathVariable Long id, @RequestBody JsonNode patch) {
        return service.patch(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
