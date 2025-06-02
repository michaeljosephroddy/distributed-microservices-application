# Distributed Microservices Application – Overview

This project is focused on building a distributed application using microservices architecture. The main goal is to demonstrate how multiple services can collaborate within a system, using various modern technologies and tools to create a robust and scalable solution.

### Key Components

- **REST-Based Microservices**: The application consists of multiple microservices, each exposing REST APIs, built with Spring Web and Spring JPA.
- **Configuration Management**: Spring Cloud Config is used for managing external configuration across the microservices, with configuration stored in a file-based repository.
- **Service Discovery**: The microservices register with Spring Cloud Netflix Eureka, allowing them to discover and communicate with each other.
- **API Gateway**: Spring Cloud API Gateway is used to route requests to the appropriate microservices, simplifying client interactions with the system.
- **Resilient Microservices**: Resilience4J is integrated to ensure the microservices can handle failures gracefully, including features like retries and circuit breakers.
- **Authentication**: OAuth2 Google sign in is used for secure authentication and authorization, protecting sensitive resources and ensuring that only authorized users can interact with the services.
- **Observability**: The system is designed with observability in mind, incorporating monitoring and logging features to track the health and performance of the services.
