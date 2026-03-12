package com.benicio.common.events;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent implements OrderEvent {
    private UUID orderId;
    private boolean success;
    private String failureReason;
    private UUID productId;
    private Integer quantity;
}
