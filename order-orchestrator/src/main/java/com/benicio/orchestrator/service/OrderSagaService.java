package com.benicio.orchestrator.service;
import com.benicio.common.events.*;
import com.benicio.orchestrator.domain.*;
import com.benicio.orchestrator.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
public class OrderSagaService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Order createOrder(Order order) {
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.CREATED);
        Order savedOrder = orderRepository.save(order);
        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(), savedOrder.getCustomerId(), savedOrder.getTotalAmount(), savedOrder.getProductId(), savedOrder.getQuantity());
        kafkaTemplate.send("orders.created", savedOrder.getId().toString(), event);
        log.info("Saga Started: Order created {}", savedOrder.getId());
        return savedOrder;
    }

    @KafkaListener(topics = "payment.processed", groupId = "order-orchestrator-group")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        if (event.isSuccess()) {
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
            kafkaTemplate.send("orders.payment-success", order.getId().toString(), event);
            log.info("Payment success for order {}. Triggering Inventory.", order.getId());
        } else {
            log.error("Payment failed for order {}", order.getId());
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setFailureReason(event.getFailureReason());
        }
        orderRepository.save(order);
    }

    @KafkaListener(topics = "inventory.processed", groupId = "order-orchestrator-group")
    @Transactional
    public void handleInventoryProcessed(InventoryProcessedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        if (event.isSuccess()) {
            order.setStatus(OrderStatus.COMPLETED);
            log.info("Order {} fully completed!", order.getId());
        } else {
            log.error("Inventory failed for order {}.", order.getId());
            order.setStatus(OrderStatus.INVENTORY_FAILED);
        }
        orderRepository.save(order);
    }
}
