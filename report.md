# **Distributed Microservices Application Report**

### **Online Store with Customer and Order Management**

## **1. Introduction**

This project is a **cloud-native online store** built using Spring Boot and a microservices architecture. It's designed to show how to structure scalable, resilient systems by keeping services separated and easy to manage.

### **Key Microservices**

- **Customer Service** – Manages customer details like name, email, address, and number of orders.
- **Order Service** – Handles everything to do with customer orders: creating, viewing, and deleting them.
- **API Gateway** – The single entry point to the system; it also manages Google login via OAuth2.
- **Service Registry** – Eureka is used so that services can find and talk to each other automatically.

### **Cloud-Native Features**

- **Eureka Service Discovery**
- **Spring Cloud Gateway**
- **Google OAuth2 Authentication**
- **Resilience4j for fault tolerance**
- **Observability with Zipkin & Spring Actuator**
- **Centralised configuration with Spring Config**

## **2. User Stories & API Endpoints**

### What the user can do

| Action                                     | Endpoint                                        | Method |
| ------------------------------------------ | ----------------------------------------------- | ------ |
| Create a customer profile                  | `/api/customers`                                | POST   |
| View all customers (with HATEOAS links)    | `/api/customers`                                | GET    |
| Delete a customer and their orders         | `/api/customers/{id}`                           | DELETE |
| Place a new order                          | `/api/orders`                                   | POST   |
| View a customer’s orders (with pagination) | `/api/orders/customer/{customerId}?page=&size=` | GET    |
| Filter orders by date range                | `/api/orders/bydate?startDate=&endDate=`        | GET    |
| Log in using Google                        | Redirected via API Gateway                      | OAuth2 |

### Observability and Monitoring

Once services are running, you can check their health, metrics, and other runtime data using Spring Boot Actuator endpoints. Replace the port with the one used by each service (e.g., `8081` for Customer Service).

| Description                     | URL                                         |
| ------------------------------- | ------------------------------------------- |
| Application health status       | `http://localhost:8081/actuator/health`     |
| Available metrics               | `http://localhost:8081/actuator/metrics`    |
| Custom app info                 | `http://localhost:8081/actuator/info`       |
| Prometheus metrics endpoint     | `http://localhost:8081/actuator/prometheus` |
| Distributed tracing with Zipkin | `http://localhost:9411/zipkin/`             |

These endpoints are handy for monitoring, debugging, and integrating with external tools like Zipkin.

## **3. Microservices Overview**

### **Customer Service**

- **Entity**: `Customer(id, name, email, address, totalOrders)`
- **Database**: MariaDB (shared across services)
- **Highlights**:
  - REST endpoints support HATEOAS
  - Deleting a customer automatically removes their orders
  - Validations: `@Validated`, required fields

### **Order Service**

- **Entity**: `Order(id, customerId, product, quantity, createdAt)`
- **Database**: Same shared MariaDB
- **Highlights**:
  - Pagination using Spring HATEOAS
  - Date range filtering
  - Validations: `@Min(1)`, `@Validated`

### **API Gateway**

- **Routes**:
  - `/api/customers/**` goes to Customer Service
  - `/api/orders/**` goes to Order Service
- **Security**:
  - Google OAuth2 for login
  - Token relay using `TokenRelay` filter

### **Service Registry (Eureka)**

- All services register with Eureka so they can discover each other dynamically.
- Configured via `eureka.client.service-url.defaultZone`.

## **4. Entities**

### **Customer.java**

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Email
    @Column(nullable = false)
    private String email;
}
```

### **Order.java**

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long customerId;
    @Column(nullable = false)
    private String product;
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;
}
```

## **5. Cloud-Native Features**

| Feature               | Where it’s configured                               | What it does                             |
| --------------------- | --------------------------------------------------- | ---------------------------------------- |
| **Service Discovery** | `eureka.client.service-url.defaultZone`             | Lets services find each other            |
| **API Gateway**       | `spring.cloud.gateway.routes`                       | Routes API requests to services          |
| **Security**          | `spring.security.oauth2.client.registration.google` | Google login integration                 |
| **Fault Tolerance**   | `resilience4j.circuitbreaker`, `retry`              | Adds circuit breakers and retries        |
| **Tracing**           | `management.tracing.zipkin.endpoint`                | Distributed request tracing with Zipkin  |
| **Metrics**           | `management.endpoints.prometheus.enabled`           | Prometheus-compatible metrics            |
| **Config Server**     | (if used)                                           | Loads central configuration for services |

## **6. API Gateway Configuration Example**

Here’s a snippet from `application.yml`:

```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: customer-service
          uri: lb://CUSTOMER-SERVICE
          predicates:
            - Path=/api/customers/**
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/orders/**
      default-filters:
        - TokenRelay

    discovery:
      locator:
        enabled: true
        lower-case-service-id: true

security:
  oauth2:
    client:
      registration:
        google:
          client-id: <your-client-id>
          client-secret: <your-client-secret>
```

## **7. Observability & Fault Tolerance**

### **Zipkin Tracing**

- Visualises the flow of a request, e.g. from the API Gateway → Order Service → Customer Service.
- Useful for spotting delays or failures in your system.

### **Resilience4J**

- Each service is protected with a circuit breaker. Example configuration:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      orderServiceCB:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

## **8. Test Results**

| What was tested                | How it was tested                      | Outcome                                   |
| ------------------------------ | -------------------------------------- | ----------------------------------------- |
| Services registered correctly  | Eureka Dashboard                       | `customer-service`, `order-service` shown |
| Secured endpoint access        | Postman with OAuth2 token              | Access granted                            |
| Paginated order list retrieval | `/api/orders/customer/1?page=0&size=5` | Worked with HATEOAS                       |
| Cascading delete works         | DELETE `/api/customers/1`              | Related orders also removed               |
| Tracing works end-to-end       | Zipkin UI                              | Full trace shown                          |

## **9. Challenges and Solutions**

### **Challenge 1: Securing Microservices with OAuth2**

**Problem:**  
Setting up OAuth2 login with Google and making sure the token is passed correctly between the API Gateway and downstream services was tricky. Without the right configuration, services would reject requests as unauthenticated.

**Solution:**  
Used Spring Security’s `TokenRelay` filter in the API Gateway to automatically forward OAuth2 tokens. This ensured downstream services could authenticate users properly. Also set up application properties to include client ID, secret, and scopes from Google.

---

### **Challenge 2: Service Communication and Discovery**

**Problem:**  
Managing how microservices communicate without hardcoding their addresses can get complex. This becomes even more difficult when scaling or restarting services, as IPs can change.

**Solution:**  
Integrated **Eureka** for service discovery. Each service registers itself on startup and looks up others using their service name. This enabled dynamic communication and easy scaling without worrying about fixed hostnames.

---

### **Challenge 3: Observability and Debugging in a Distributed Setup**

**Problem:**  
When multiple services are involved in a single request, tracking where something went wrong is difficult without proper observability tools.

**Solution:**  
Added **Zipkin** for distributed tracing and for metrics collection. With Zipkin, it became easy to trace a request’s full journey across services. Spring Actuator endpoints provided visibility into health and performance.

## **10. Conclusion**

This microservices-based online store project shows how to:

- Build independent, scalable services
- Secure them with Google login
- Make them resilient to failure
- Monitor and trace them easily

With a few more additions—like Swagger docs and automated testing—it could be ready for real-world use in small to mid-sized online shops or internal systems.

## References

- [Spring Cloud Gateway – Official Docs](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Spring Cloud Netflix – Official Docs](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Spring Data JPA – The Persistence Layer (Baeldung)](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)
- [Beginner’s Guide to Spring Cloud Config (Medium)](https://medium.com/@AlexanderObregon/a-beginners-guide-to-centralized-configuration-with-spring-cloud-config-6dfb6c70b5ad#:~:text=Centralized%20configuration%20is%20an%20approach,%2C%20Subversion%2C%20or%20file%20system)
- [Centralised Configuration – Spring Guide](https://spring.io/guides/gs/centralized-configuration)
- [Spring Boot + OAuth2 – Secure Your App (Spring Guide)](https://spring.io/guides/tutorials/spring-boot-oauth2)
- [Spring Cloud Gateway – Overview and Setup (Baeldung)](https://www.baeldung.com/spring-cloud-gateway)
- [Observability with Micrometer and Zipkin (Medium)](https://medium.com/@yusuf.aziz/observability-with-micrometer-and-zipkin-44b29315a16f)
- [Intro to Distributed System Patterns in Spring Boot (DZone)](https://dzone.com/articles/intro-to-distributed-system-patterns-in-spring-boot)
