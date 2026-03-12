package com.benicio.common.events;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements OrderEvent {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private UUID productId;
    private Integer quantity;
}
