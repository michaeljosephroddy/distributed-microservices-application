package com.example.order_service.model;

import java.time.LocalDateTime;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.data.annotation.CreatedDate;
// import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// import com.fasterxml.jackson.annotation.JsonFormat;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;

public class Customer {
    private Long id;
    private String name;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private Integer totalOrders;

    public Customer() {
    }

    public Customer(Long id, String name, String email, String address, LocalDateTime createdAt,
            Integer totalOrders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
        this.totalOrders = totalOrders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalOrders() {
        return totalOrders != null ? totalOrders : 0;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    @Override
    public String toString() {
        return "Customer [toString()=" + super.toString() + "]";
    }

}
