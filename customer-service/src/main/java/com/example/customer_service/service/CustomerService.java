package com.example.customer_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.customer_service.exception.BadRequestException;
import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.exception.ServiceUnavailableException;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RestTemplate restTemplate;

    /**
     * Creates a new customer.
     *
     * @param customer The customer object to be created.
     * @return The created customer.
     */
    public Customer createCustomer(Customer customer) {
        logger.info("Entering createCustomer method with customer: {}", customer);
        if (customer == null || customer.getName() == null || customer.getName().isEmpty()) {
            logger.error("Customer name is null or empty");
            throw new BadRequestException("Customer name cannot be null or empty.");
        }
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully: {}", savedCustomer);
        return savedCustomer;
    }

    /**
     * Retrieves a customer by ID.
     *
     * @param customerId The ID of the customer to retrieve.
     * @return The found customer.
     * @throws ResourceNotFoundException if the customer is not found.
     */
    public Customer getCustomerById(Long customerId) {
        logger.info("Fetching customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", customerId);
                    return new ResourceNotFoundException("Customer with ID " + customerId + " not found.");
                });
        logger.info("Customer fetched successfully: {}", customer);
        return customer;
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of all customers.
     */
    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Retrieved {} customers", customers.size());
        return customers;
    }

    /**
     * Deletes a customer and all associated orders.
     *
     * @param customerId The ID of the customer to be deleted.
     * @throws ResourceNotFoundException if the customer does not exist.
     */
    public void deleteCustomer(Long customerId) {
        logger.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Cannot delete. Customer with ID {} not found.", customerId);
                    return new ResourceNotFoundException(
                            "Cannot delete. Customer with ID " + customerId + " not found.");
                });

        // Delete all orders associated with the customer
        // This is a call to the order service to delete all orders for this customer
        logger.info("Deleting all orders for customer with ID: {}", customerId);
        deleteAllOrders(customerId);

        // Delete the customer
        customerRepository.deleteById(customerId);
        logger.info("Customer with ID {} deleted successfully", customerId);
    }

    /**
     * Deletes all orders associated with a customer.
     * This method uses Resilience4j to handle circuit breaking and retry logic
     * This is a call to the order service to delete all orders for this customer
     *
     * @param customerId The ID of the customer whose orders are to be deleted.
     * @throws ServiceUnavailableException if the order service is unavailable.
     */
    @CircuitBreaker(name = "customerServiceCB", fallbackMethod = "deleteAllOrdersFallback")
    @Retry(name = "customerServiceRetry")
    public void deleteAllOrders(Long customerId) {
        logger.info("Calling order service to delete all orders for customer with ID: {}", customerId);
        String orderServiceUrl = "http://order-service/api/orders/deleteall/" + customerId;
        restTemplate.delete(orderServiceUrl);
        logger.info("Successfully deleted all orders for customer with ID: {}", customerId);
    }

    // fallback method if circuit is open or request fails
    private void deleteAllOrdersFallback(Long customerId, Throwable ex) {
        logger.error("Fallback triggered for deleteAllOrders with customerId: {}. Reason: {}", customerId,
                ex.getMessage());
        throw new ServiceUnavailableException("Order service is currently unavailable. Please try again later.");
    }

    /**
     * Retrieves a list of customers sorted by creation date.
     *
     * @param sortDirection The sorting direction, either "asc" for ascending or
     *                      "desc" for descending.
     * @return A list of customers sorted by their creation date in the specified
     *         order.
     */
    public List<Customer> getCustomersSorted(String sortDirection) {
        logger.info("Fetching customers sorted by creation date in {} order", sortDirection);
        List<Customer> customers = "asc".equalsIgnoreCase(sortDirection)
                ? customerRepository.findAllByCreatedAtAsc()
                : customerRepository.findAllByCreatedAtDesc();
        logger.info("Retrieved {} customers sorted in {} order", customers.size(), sortDirection);
        return customers;
    }
}
