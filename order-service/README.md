# Development and Demonstration of a REST API

### 1. Overview

Developing a REST API using Spring Boot to manage customer orders, focusing on core microservices principles. The app includes two primary entities: Customer and Order, linked by a one-to-many relationship. The API supports CRUD operations, pagination, date filtering, and HATEOAS for hypermedia-driven navigation.

### 1.1 Objectives

1. Implement a scalable REST API adhering to RESTful standards.

2. Model a one-to-many relationship between customers and orders.

3. Use DTOs to decouple internal data models from external API responses.

4. Handle date/time inputs using LocalDateTime with proper serialisation.

5. Integrate HATEOAS to enable client navigation through hypermedia links.

6. Implement error handling for validation and exceptions.

## 2. Design and Implementation

### 2.1 REST API Design

#### One-to-Many Relationship

The API models a Customer-Order relationship:

Customer (/api/customers): A customer can create multiple orders.

Order (/api/orders): Each order is linked to a customer by customerId.

Example Workflow:

1. A POST request to `/api/customers creates a customer`.

2. A POST request to `/api/orders` creates an order with the customer’s ID.

3. A GET request to `/api/orders/customer/{customerId}` retrieves all orders for that customer.

#### Date Handling

LocalDateTime is used for timestamps (e.g., order creation time).

The @JsonFormat annotation ensures consistent date serialisation:

```java
@CreatedDate
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createdAt;
```

Endpoints like `/api/orders/bydate` filter orders by date range using startDate and endDate.

#### DTOs (Data Transfer Objects)

DTOs simplify responses by exposing only necessary fields:

CustomerDTO excludes sensitive fields like address.

OrderDTO focuses on id, createdAt, and quantity.

Why DTOs?

Decouple internal database schema from external API contracts.

Improve security by hiding implementation details.

### 2.2 Database Design

#### Entity-Relationship Diagram (ERD)

```sql
+----------------+          +----------------+
|   Customer     |          |     Order      |
+----------------+          +----------------+
| id (PK)        |<-----+   | id (PK)        |
| name           |      |-->| customer_id(FK)|
| email          |          | product        |
| address        |          | quantity       |
| created_at     |          | created_at     |
| total_orders   |          +----------------+
+----------------+
```

#### Tables

customers

```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    created_at TIMESTAMP,
    total_orders INT
);
```

orders

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    product VARCHAR(255),
    quantity INT,
    created_at TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### 2.3 Error Handling

Validation Errors:  
Annotated with @Validated in controllers.

Example:

```java
@PostMapping
public ResponseEntity<CustomerDTO> createCustomer(@Validated @RequestBody Customer customer) { ... }
```

Invalid requests trigger 400 Bad Request.

Custom Exceptions:

ResourceNotFoundException: Thrown when a resource is not found (e.g., invalid customerId).

BadRequestException: Handles invalid inputs (e.g., null customer name).

Example Exception Response:

```json
{
  "timestamp": "2023-11-05T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer with ID 99 not found."
}
```

### 2.4 HATEOAS Implementation

Hypermedia Links are embedded in responses using Spring HATEOAS.

For example:

The CustomerModelAssembler generates links for CustomerDTO responses:

```java
public class CustomerModelAssembler extends RepresentationModelAssemblerSupport<Customer, EntityModel<CustomerDTO>> {
    @Override
    public EntityModel<CustomerDTO> toModel(Customer customer) {
        CustomerDTO dto = new CustomerDTO(...);
        return EntityModel.of(dto,
            linkTo(methodOn(CustomerController.class).getCustomerById(customer.getId())).withSelfRel(),
            linkTo(methodOn(OrderController.class).getAllOrders(customer.getId(), 0, 10)).withRel("orders"));
    }
}
```

Sample HATEOAS Response:

```json
{
  "id": 1,
  "name": "Alice Brown",
  "email": "alice@example.com",
  "totalOrders": 2,
  "_links": {
    "self": { "href": "http://localhost:8080/api/customers/1" },
    "orders": {
      "href": "http://localhost:8080/api/orders/customer/1?page=0&size=10"
    }
  }
}
```

## 3. Code Explanation

### 3.1 Layers and Architecture

#### Controller Layer

CustomerController:

Maps endpoints like POST `/api/customers` and GET `/api/customers/{id}`.

OrderController:

Supports pagination via Pageable:

```java
@GetMapping("/customer/{customerId}")
public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> getAllOrders(
    @PathVariable Long customerId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) { ... }
```

#### Service Layer

CustomerService:

Validates inputs (e.g., non-empty customer name).

Cascades deletes: When a customer is deleted, all associated orders are removed:

```java
public void deleteCustomer(Long customerId) {
    orderRepository.deleteByCustomerId(customerId);
    customerRepository.deleteById(customerId);
}
```

OrderService:

Implements date filtering:

```java
public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    return orderRepository.findByCreatedAtBetween(startDate, endDate);
}
```

#### Repository Layer

Uses Spring Data JPA interfaces (CustomerRepository, OrderRepository) for database operations.

### 3.2 Key Code Snippets

One-to-Many Relationship in Order Entity:

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id")
    private Long customerId;
}
```

DTO Implementation (OrderDTO):

```java
public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private Integer quantity;
}
```

## 4. Challenges and Solutions

### 4.1 Challenge: Cascading Deletes

Issue: Deleting a customer without removing orders caused foreign key constraint violations.

Solution: Explicitly delete orders first using orderRepository.deleteByCustomerId(customerId).

### 4.2 Challenge: Date Serialisation

Issue: LocalDateTime was serialised in an unreadable format (e.g., 2023-11-05T14:30:00.000Z).

Solution: Added @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") to format timestamps.

### 4.3 Challenge: HATEOAS Pagination

Issue: Generating HATEOAS links for paginated order lists.

Solution: Used PagedResourcesAssembler to wrap paginated results with navigation links:

```java
@Autowired
private PagedResourcesAssembler<Order> pagedResourcesAssembler;
public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> getAllOrders(...) {
    Page<Order> orders = orderService.getAllOrders(customerId, pageable);
    PagedModel<EntityModel<OrderDTO>> pagedModel = pagedResourcesAssembler.toModel(orders, orderModelAssembler);
    return ResponseEntity.ok(pagedModel);
}
```

## 7. Testing Strategy

This application follows a comprehensive testing strategy based on the **Test Pyramid**, ensuring that the system is thoroughly tested at all levels. The pyramid emphasises having a larger number of fast, isolated unit tests, a moderate number of integration tests, and a smaller number of acceptance tests.

### 7.1 Test Pyramid Overview

The **Test Pyramid** is a best-practice testing strategy that balances different types of tests to achieve high-quality software:

1. **Unit Tests** (Base of the Pyramid):

   - Fast and isolated tests that validate individual components (e.g., services, utilities).
   - Focus on testing business logic in isolation without external dependencies.
   - High volume of tests to cover edge cases and ensure correctness.

2. **Integration Tests** (Middle of the Pyramid):

   - Validate the interaction between components (e.g., controllers, services, repositories).
   - Use an in-memory database (H2) to simulate real-world scenarios.
   - Moderate volume of tests to ensure components work together as expected.

3. **Acceptance Tests** (Top of the Pyramid):
   - High-level tests that validate end-to-end workflows and business requirements.
   - Simulate real-world usage of the application through API calls.
   - Smaller volume of tests to ensure critical workflows meet user expectations.

---

### 7.2 Unit Testing

- **Purpose**: Validate the correctness of individual components in isolation.
- **Tools**: JUnit 5, Mockito.
- **Scope**:
  - Service layer methods (e.g., `CustomerService`, `OrderService`).
  - Utility classes and helper methods.
- **Example**:

  ```java
  @Test
  void calculateTotalOrders_ShouldReturnCorrectCount() {
      // Given
      List<Order> orders = List.of(new Order(...), new Order(...));

      // When
      int totalOrders = orderService.calculateTotalOrders(orders);

      // Then
      assertEquals(2, totalOrders);
  }
  ```

---

### 7.3 Integration Testing

- **Purpose**: Validate the interaction between components (e.g., controllers, services, repositories).
- **Tools**: Spring Boot Test, MockMvc, H2 in-memory database.
- **Scope**:
  - Controller endpoints (e.g., `/api/customers`, `/api/orders`).
  - Repository queries and database interactions.
  - Service layer logic with real dependencies.
- **Example**:

  ```java
  @Test
  void createCustomer_ShouldPersistCustomer() throws Exception {
      // Given
      Map<String, Object> customerRequest = new HashMap<>();
      customerRequest.put("name", "John Doe");
      customerRequest.put("email", "john.doe@example.com");
      customerRequest.put("address", "123 Test Street");

      // When & Then
      mockMvc.perform(post("/api/customers")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(customerRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").exists());
  }
  ```

---

### 7.4 Acceptance Testing

- **Purpose**: Validate end-to-end workflows and ensure the application meets business requirements.
- **Tools**: Spring MockMvc, ObjectMapper.
- **Scope**:
  - High-level workflows, such as creating a customer, placing an order, and retrieving orders.
  - Simulate real-world API usage scenarios.
- **Example**:

  ```java
  @Test
  void createCustomerAndPlaceOrder_ShouldReturnOrderDetails() throws Exception {
      // Given
      Map<String, Object> customerRequest = new HashMap<>();
      customerRequest.put("name", "Alice");
      customerRequest.put("email", "alice@example.com");
      customerRequest.put("address", "123 Test Lane");

      String customerResponse = mockMvc.perform(post("/api/customers")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(customerRequest)))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long customerId = objectMapper.readTree(customerResponse).get("id").asLong();

      Map<String, Object> orderRequest = new HashMap<>();
      orderRequest.put("customerId", customerId);
      orderRequest.put("product", "Test Product");
      orderRequest.put("quantity", 2);

      // When & Then
      mockMvc.perform(post("/api/orders")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(orderRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.product").value("Test Product"))
              .andExpect(jsonPath("$.quantity").value(2));
  }
  ```

## 5. Conclusion

This project successfully implemented a RESTful API using Spring Boot, demonstrating key microservices concepts such as layered architecture, DTOs, HATEOAS, and error handling.

Future improvements could include:

1. Adding authentication/authorisation via Spring Security.

2. Implementing Spring Data REST for automated HATEOAS.

3. Integrating Swagger for API documentation.

## 6. References

Carnell, J. (2017) Spring Microservices in Action. Shelter Island, NY: Manning Publications.

Spring Boot Documentation (n.d.) Spring Boot Reference Documentation. Available at: https://docs.spring.io/spring-boot/documentation.html (Accessed: 24 February 2025).

Spring Data JPA Documentation (n.d.) Getting Started with Spring Data JPA. Available at: https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html (Accessed: 24 February 2025).

Spring HATEOAS Documentation (n.d.) Spring HATEOAS Reference Documentation. Available at: https://docs.spring.io/spring-hateoas/docs/current/reference/html/ (Accessed: 24 February 2025).

Baeldung (2013) Exception Handling in Spring MVC. Available at: https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc (Accessed: 24 February 2025).

Baeldung (n.d.) Exception Handling for REST with Spring. Available at: https://www.baeldung.com/exception-handling-for-rest-with-spring (Accessed: 24 February 2025).

Baeldung (n.d.) Entity to and from DTO for a Java Spring Application. Available at: https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application (Accessed: 24 February 2025).

Baeldung (n.d.) Spring Boot Paged Query – Retrieve All Results. Available at: https://www.baeldung.com/spring-boot-paged-query-all-results (Accessed: 24 February 2025).
