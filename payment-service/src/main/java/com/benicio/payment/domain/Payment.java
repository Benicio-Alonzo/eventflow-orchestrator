package com.benicio.payment.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name = "payments") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
}
