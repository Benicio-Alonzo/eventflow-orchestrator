package com.benicio.inventory.repository;
import com.benicio.inventory.domain.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
public interface InventoryRepository extends JpaRepository<ProductInventory, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductInventory p WHERE p.productId = :id")
    Optional<ProductInventory> findByIdWithLock(@Param("id") UUID id);
}
