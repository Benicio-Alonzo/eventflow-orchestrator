package com.benicio.payment.service;
import com.benicio.common.events.*;
import com.benicio.payment.domain.Payment;
import com.benicio.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
public class PaymentListener {
    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "orders.created", groupId = "payment-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received payment request for order: {}", event.getOrderId());
        if (repository.existsByOrderId(event.getOrderId())) {
            log.warn("Payment already processed for order {}", event.getOrderId());
            return;
        }
        boolean isApproved = event.getTotalAmount().compareTo(new BigDecimal("10000")) < 0;
        Payment payment = Payment.builder().id(UUID.randomUUID()).orderId(event.getOrderId()).amount(event.getTotalAmount()).status(isApproved ? "AUTHORIZED" : "DECLINED").build();
        repository.save(payment);
        PaymentProcessedEvent response = new PaymentProcessedEvent(event.getOrderId(), isApproved, isApproved ? null : "Limit Exceeded", event.getProductId(), event.getQuantity());
        kafkaTemplate.send("payment.processed", event.getOrderId().toString(), response);
        log.info("Payment processed. Success: {}", isApproved);
    }
}
