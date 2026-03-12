package com.benicio.orchestrator.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name = "orders") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id private UUID id;
    private UUID customerId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) private OrderStatus status;
    private String failureReason;
}
