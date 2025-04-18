package com.example.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.order_service.exception.BadRequestException;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.model.Order;
// import com.example.order_service.model.Customer;
import com.example.order_service.repository.OrderRepository;
// import com.example.order_service.repository.CustomerRepository;
import com.example.order_service.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    // @Mock
    // private CustomerRepository customerRepository; // Add this mock

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    // private Customer testCustomer;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        // testCustomer = new Customer();
        // testCustomer.setId(1L);
        // testCustomer.setName("Test Customer");
        // testCustomer.setEmail("test@example.com");
        // testCustomer.setAddress("123 Test Lane");
        // testCustomer.setCreatedAt(testDateTime);
        // testCustomer.setTotalOrders(0);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerId(1L);
        testOrder.setProduct("Test Product");
        testOrder.setQuantity(2);
        testOrder.setCreatedAt(testDateTime);
    }

    // @Test
    // void createOrder_WithValidOrder_ShouldReturnSavedOrder() {
    // // Given
    // when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    // // Mock customer existence
    // when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

    // // When
    // Order savedOrder = orderService.createOrder(testOrder);

    // // Then
    // assertNotNull(savedOrder);
    // assertEquals(testOrder.getId(), savedOrder.getId());
    // assertEquals(testOrder.getProduct(), savedOrder.getProduct());
    // verify(customerRepository, times(1)).findById(1L); // Verify customer lookup
    // verify(orderRepository, times(1)).save(any(Order.class));
    // }

    @Test
    void createOrder_WithNullOrder_ShouldThrowBadRequestException() {
        // When & Then
        assertThrows(BadRequestException.class, () -> orderService.createOrder(null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithNullProduct_ShouldThrowBadRequestException() {
        // Given
        testOrder.setProduct(null);

        // When & Then
        assertThrows(BadRequestException.class, () -> orderService.createOrder(testOrder));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithNegativeQuantity_ShouldThrowBadRequestException() {
        // Given
        testOrder.setQuantity(-1);

        // When & Then
        assertThrows(BadRequestException.class, () -> orderService.createOrder(testOrder));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // @Test
    // void getAllOrders_WithExistingCustomer_ShouldReturnOrders() {
    // // Given
    // Pageable pageable = PageRequest.of(0, 10);
    // Page<Order> ordersPage = new PageImpl<>(Arrays.asList(testOrder));
    // when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    // // Mock customer existence
    // when(orderRepository.findByCustomerId(1L, pageable)).thenReturn(ordersPage);

    // // When
    // Page<Order> result = orderService.getAllOrders(1L, pageable);

    // // Then
    // assertNotNull(result);
    // assertEquals(1, result.getContent().size());
    // verify(customerRepository, times(1)).findById(1L); // Verify customer lookup
    // verify(orderRepository, times(1)).findByCustomerId(1L, pageable);
    // }

    // @Test
    // void
    // getAllOrders_WithNonExistingCustomer_ShouldThrowResourceNotFoundException() {
    // // Given
    // Pageable pageable = PageRequest.of(0, 10);
    // when(customerRepository.findById(1L)).thenReturn(Optional.empty()); // Mock
    // non-existing customer

    // // When & Then
    // assertThrows(ResourceNotFoundException.class, () ->
    // orderService.getAllOrders(1L, pageable));
    // verify(customerRepository, times(1)).findById(1L); // Verify customer lookup
    // verify(orderRepository, never()).findByCustomerId(anyLong(),
    // any(Pageable.class));
    // }

    @Test
    void getOrder_WithExistingId_ShouldReturnOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order foundOrder = orderService.getOrder(1L);

        // Then
        assertNotNull(foundOrder);
        assertEquals(testOrder.getId(), foundOrder.getId());
        assertEquals(testOrder.getProduct(), foundOrder.getProduct());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrder_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void updateOrder_WithValidOrder_ShouldReturnUpdatedOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order updatedOrder = orderService.updateOrder(testOrder);

        // Then
        assertNotNull(updatedOrder);
        assertEquals(testOrder.getId(), updatedOrder.getId());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_WithNullOrder_ShouldThrowBadRequestException() {
        // When & Then
        assertThrows(BadRequestException.class, () -> orderService.updateOrder(null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void deleteOrder_WithExistingId_ShouldDeleteOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).deleteById(1L);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrder_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
