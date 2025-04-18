package com.example.order_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.order_service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order save(Order order);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Order> findById(Long id);

    void deleteById(Long id);

    void deleteByCustomerId(Long id);

    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt ASC")
    List<Order> findAllByCreatedAtAsc();

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllByCreatedAtDesc();

}
