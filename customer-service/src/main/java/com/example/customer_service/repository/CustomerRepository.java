package com.example.customer_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.customer_service.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @SuppressWarnings({ "null", "unchecked" })
    Customer save(Customer customer);

    List<Customer> findAll();

    void deleteById(Long customerId);

    @Query("SELECT c FROM Customer c ORDER BY c.createdAt ASC")
    List<Customer> findAllByCreatedAtAsc();

    @Query("SELECT c FROM Customer c ORDER BY c.createdAt DESC")
    List<Customer> findAllByCreatedAtDesc();

    Optional<Customer> findById(Long customerId);

}
