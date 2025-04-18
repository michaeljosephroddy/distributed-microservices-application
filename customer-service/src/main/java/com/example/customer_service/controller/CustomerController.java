package com.example.customer_service.controller;

import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.hateoas.CustomerModelAssembler;
import com.example.customer_service.model.Customer;
import com.example.customer_service.model.CustomerDTO;
import com.example.customer_service.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling customer-related operations.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerModelAssembler customerModelAssembler;

    /**
     * Creates a new customer.
     *
     * @param customer The customer object to be created.
     * @return The created customer as a DTO.
     */
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Validated @RequestBody Customer customer) {
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(new CustomerDTO(savedCustomer.getId(), savedCustomer.getName(),
                savedCustomer.getEmail(), savedCustomer.getTotalOrders()));
    }

    /**
     * Retrieves all customers.
     *
     * @return A list of all customers as DTOs, each with HATEOAS links.
     */
    @GetMapping
    public ResponseEntity<List<EntityModel<CustomerDTO>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<EntityModel<CustomerDTO>> customerDTOs = customers.stream()
                .map(customerModelAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customerDTOs);
    }

    /**
     * Retrieves a specific customer by ID.
     *
     * @param id The ID of the customer.
     * @return The customer details with HATEOAS links.
     * @throws ResourceNotFoundException if the customer is
     *                                   not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CustomerDTO>> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerModelAssembler.toModel(customer));
    }

    /**
     * Deletes a customer and all associated orders.
     *
     * @param id The ID of the customer to delete.
     * @return A response message confirming the deletion.
     * @throws ResourceNotFoundException if the customer does
     *                                   not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Cascading delete successful");
    }
}
