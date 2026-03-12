package com.benicio.inventory.service;
import com.benicio.common.events.*;
import com.benicio.inventory.domain.ProductInventory;
import com.benicio.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
public class InventoryListener {
    private final InventoryRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "orders.payment-success", groupId = "inventory-service-group")
    @Transactional
    public void handlePaymentSuccess(PaymentProcessedEvent event) {
        log.info("Attempting to reserve inventory for order: {}", event.getOrderId());
        UUID productId = event.getProductId(); 
        int quantity = event.getQuantity();
        boolean success = false;
        String failureReason = null;
        try {
            ProductInventory inventory = repository.findByIdWithLock(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            if (inventory.getAvailableQuantity() >= quantity) {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
                repository.save(inventory);
                success = true;
                log.info("Inventory reserved. Remaining: {}", inventory.getAvailableQuantity());
            } else {
                failureReason = "Insufficient Stock";
                log.warn("Insufficient stock for product {}", productId);
            }
        } catch (Exception e) {
            failureReason = e.getMessage();
            log.error("Inventory error", e);
        }
        InventoryProcessedEvent response = new InventoryProcessedEvent(event.getOrderId(), success, failureReason);
        kafkaTemplate.send("inventory.processed", event.getOrderId().toString(), response);
    }
}
