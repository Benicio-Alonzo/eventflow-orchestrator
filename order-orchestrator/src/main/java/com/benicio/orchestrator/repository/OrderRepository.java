package com.benicio.orchestrator.repository;
import com.benicio.orchestrator.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OrderRepository extends JpaRepository<Order, UUID> {}
