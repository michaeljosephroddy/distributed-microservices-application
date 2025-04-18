package com.example.order_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order_service.exception.BadRequestException;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.exception.ServiceUnavailableException;
import com.example.order_service.model.Customer;
import com.example.order_service.model.Order;
// import com.example.order_service.repository.CustomerRepository;
import com.example.order_service.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

/**
 * Service class for managing business logic related to orders.
 * This class acts as an intermediary between the controller and repository
 * layers.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderRepository orderRepository;
    // @Autowired
    // CustomerRepository customerRepository;

    @Autowired
    RestTemplate restTemplate;

    /**
     * Creates a new order.
     *
     * @param order The order object to be created.
     * @return The created order.
     * @throws BadRequestException if the order object is null or invalid.
     */
    public Order createOrder(Order order) {
        logger.info("Entering createOrder method with order: {}", order);
        if (order == null) {
            logger.error("Order is null");
            throw new BadRequestException("Order cannot be null");
        }
        if (order.getProduct() == null || order.getProduct().isEmpty()) {
            logger.error("Product is null or empty");
            throw new BadRequestException("Product cannot be null or empty");
        }
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            logger.error("Quantity is invalid: {}", order.getQuantity());
            throw new BadRequestException("Quantity must be greater than 0");
        }

        // service discovery
        // Validate customer existence using RestTemplate
        ResponseEntity<Customer> customer = getCustomerById(order.getCustomerId());
        logger.info("Customer validated for order creation: {}", customer.getBody());

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully: {}", savedOrder);
        return savedOrder;
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param id The ID of the customer.
     * @return A list of all orders for the specified customer.
     */
    public Page<Order> getAllOrders(Long customerId, Pageable pageable) {
        logger.info("Entering getAllOrders method for customerId: {}", customerId);
        // Validate customer existence using RestTemplate
        ResponseEntity<Customer> customer = getCustomerById(customerId);
        logger.info("Customer validated for retrieving orders: {}", customer.getBody());

        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        logger.info("Retrieved {} orders for customerId: {}", orders.getTotalElements(), customerId);
        return orders;
    }

    // circuit breaker prevents your app from calling a downstream broken service
    // again and again.
    @CircuitBreaker(name = "orderServiceCB", fallbackMethod = "getCustomerByIdFallback")
    @Retry(name = "orderServiceRetry")
    public ResponseEntity<Customer> getCustomerById(Long customerId) {
        logger.info("Fetching customer with ID: {}", customerId);
        String customerServiceUrl = "http://customer-service/api/customers/" + customerId;
        ResponseEntity<Customer> response = restTemplate.getForEntity(customerServiceUrl, Customer.class);
        logger.info("Customer fetched successfully: {}", response.getBody());
        return response;
    }

    // fallback method gets called when retries/circuit breaker fail
    private ResponseEntity<Customer> getCustomerByIdFallback(Long customerId, Throwable ex) {
        logger.error("Fallback triggered for getCustomerById with customerId: {}. Reason: {}", customerId,
                ex.getMessage());
        throw new ServiceUnavailableException("Customer service is unavailable. Please try again later.");
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param id The ID of the order.
     * @return The order with the specified ID.
     * @throws ResourceNotFoundException if the order is not found.
     */
    public Order getOrder(Long id) {
        logger.info("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Order not found with id: " + id);
                });
        logger.info("Order fetched successfully: {}", order);
        return order;
    }

    /**
     * Updates an existing order.
     *
     * @param order The order object containing updated details.
     * @return The updated order.
     * @throws ResourceNotFoundException if the order does not exist.
     * @throws BadRequestException       if the order object is null or invalid.
     */
    public Order updateOrder(Order order) {
        logger.info("Updating order: {}", order);
        if (order == null || order.getId() == null) {
            logger.error("Invalid order: order or ID is null");
            throw new BadRequestException("Invalid order: order and ID must not be null");
        }

        orderRepository.findById(order.getId())
                .orElseThrow(() -> {
                    logger.error("Cannot update: Order not found with ID: {}", order.getId());
                    return new ResourceNotFoundException("Cannot update: Order not found with id: " + order.getId());
                });

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order updated successfully: {}", updatedOrder);
        return updatedOrder;
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to be deleted.
     * @throws ResourceNotFoundException if the order does not exist.
     */
    public void deleteOrder(Long id) {
        logger.info("Deleting order with ID: {}", id);
        orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot delete: Order not found with ID: {}", id);
                    return new ResourceNotFoundException("Cannot delete: Order not found with id: " + id);
                });

        orderRepository.deleteById(id);
        logger.info("Order deleted successfully with ID: {}", id);
    }

    /**
     * Deletes all orders associated with a given customer.
     *
     * @param customerId The ID of the customer whose orders should be deleted.
     */
    public void deleteAllOrders(Long customerId) {
        logger.info("Deleting all orders for customerId: {}", customerId);
        orderRepository.deleteByCustomerId(customerId); // Delete orders first
        logger.info("All orders deleted for customerId: {}", customerId);
    }

    /**
     * Retrieves a list of orders within a specified date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of orders created within the specified date range.
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching orders between {} and {}", startDate, endDate);
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        logger.info("Retrieved {} orders between {} and {}", orders.size(), startDate, endDate);
        return orders;
    }

    /**
     * Retrieves a list of orders sorted by creation date.
     *
     * @param sortDirection The sorting direction, either "asc" for ascending or
     *                      "desc" for descending.
     * @return A list of orders sorted by their creation date in the specified
     *         order.
     */
    public List<Order> getOrdersSorted(String sortDirection) {
        logger.info("Fetching orders sorted by creation date in {} order", sortDirection);
        List<Order> orders = "asc".equalsIgnoreCase(sortDirection)
                ? orderRepository.findAllByCreatedAtAsc()
                : orderRepository.findAllByCreatedAtDesc();
        logger.info("Retrieved {} orders sorted in {} order", orders.size(), sortDirection);
        return orders;
    }
}
