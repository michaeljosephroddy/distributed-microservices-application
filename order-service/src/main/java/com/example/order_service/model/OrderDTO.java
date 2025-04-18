package com.example.order_service.model;

import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private Integer quantity;

    public OrderDTO() {
    }

    public OrderDTO(Long id, LocalDateTime createdAt, Integer quantity) {
        this.id = id;
        this.createdAt = createdAt;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderDTO [toString()=" + super.toString() + "]";
    }

}
