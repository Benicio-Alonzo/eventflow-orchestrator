package com.benicio.orchestrator.controller;
import com.benicio.orchestrator.domain.Order;
import com.benicio.orchestrator.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderSagaService sagaService;
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(sagaService.createOrder(order));
    }
}
