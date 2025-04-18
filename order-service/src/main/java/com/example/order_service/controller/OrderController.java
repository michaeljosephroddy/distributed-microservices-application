package com.example.order_service.controller;

import com.example.order_service.exception.BadRequestException;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.hateoas.OrderModelAssembler;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderDTO;
import com.example.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for handling order-related operations.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderModelAssembler orderModelAssembler;

    @Autowired
    private PagedResourcesAssembler<Order> pagedResourcesAssembler;

    /**
     * Creates a new order.
     *
     * @param order The order object to be created.
     * @return The created order as a DTO.
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Validated @RequestBody Order order) {
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(new OrderDTO(savedOrder.getId(), savedOrder.getCreatedAt(), savedOrder.getQuantity()));
    }

    /**
     * Retrieves a Page of orders for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A page of orders for the specified customer as DTOs with HATEOAS
     *         links.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> getAllOrders(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllOrders(customerId, pageable);

        PagedModel<EntityModel<OrderDTO>> pagedModel = pagedResourcesAssembler
                .toModel(orders, orderModelAssembler);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Retrieves a specific order by ID.
     *
     * @param id The ID of the order.
     * @return The order details with HATEOAS links.
     * @throws ResourceNotFoundException if the order is not
     *                                   found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderDTO>> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(orderModelAssembler.toModel(order));
    }

    /**
     * Updates an existing order.
     *
     * @param order The order object containing updated details.
     * @return The updated order as a DTO.
     * @throws BadRequestException       if the order details
     *                                   are invalid.
     * @throws ResourceNotFoundException if the order does not
     *                                   exist.
     */
    @PutMapping
    public ResponseEntity<OrderDTO> updateOrder(@Validated @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity
                .ok(new OrderDTO(updatedOrder.getId(), updatedOrder.getCreatedAt(), updatedOrder.getQuantity()));
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to be deleted.
     * @return A response message confirming deletion.
     * @throws ResourceNotFoundException if the order does not
     *                                   exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    /**
     * Deletes all orders for a customer.
     *
     * @param id The ID of the customer to delete orders for.
     * @return A response message confirming deletion.
     * @throws ResourceNotFoundException if the order does not
     *                                   exist.
     */
    @DeleteMapping("/deleteall/{id}")
    public ResponseEntity<String> deleteOrders(@PathVariable Long id) {
        orderService.deleteAllOrders(id);
        return ResponseEntity.ok("All Orders deleted successfully for customer ID: " + id);
    }

    /**
     * Retrieves orders within a specific date range.
     *
     * @param startDate The start date-time of the range.
     * @param endDate   The end date-time of the range.
     * @return A list of orders within the specified date range.
     */
    @GetMapping("/bydate")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate));
    }

    /**
     * Retrieves a list of orders sorted by creation date.
     *
     * @param sortDirection The sorting direction (asc/desc).
     * @return A sorted list of orders.
     */
    @GetMapping("/sorted")
    public ResponseEntity<List<Order>> getOrdersSorted(@RequestParam String sortDirection) {
        return ResponseEntity.ok(orderService.getOrdersSorted(sortDirection));
    }
}
