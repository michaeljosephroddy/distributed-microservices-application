#!/bin/bash

# Function to wait for service to be ready
wait_for_service() {
  local name=$1
  local port=$2
  echo "Waiting for $name to be available on port $port..."

  while ! nc -z localhost $port; do
    sleep 2
  done

  echo "$name is up!"
}

# Start config-server
echo "Starting config-server..."
cd config-server || exit
mvn clean install
mvn spring-boot:run &
CONFIG_PID=$!
cd ..

# Wait for config-server (assuming port 8888)
wait_for_service "config-server" 8888

# Start service-registry (Eureka)
echo "Starting service-registry..."
cd service-registry || exit
mvn clean install
mvn spring-boot:run &
REGISTRY_PID=$!
cd ..

wait_for_service "service-registry" 8761

# Start api-gateway
echo "Starting api-gateway..."
cd api-gateway || exit
mvn clean install
mvn spring-boot:run &
GATEWAY_PID=$!
cd ..

wait_for_service "api-gateway" 8085

# Start customer-service
echo "Starting customer-service..."
cd customer-service || exit
mvn clean install
mvn spring-boot:run &
CUSTOMER_PID=$!
cd ..

wait_for_service "customer-service" 8082

# Start order-service
echo "Starting order-service..."
cd order-service || exit
mvn clean install
mvn spring-boot:run &
CUSTOMER_PID=$!
cd ..

wait_for_service "order-service" 8081

echo "All services started successfully!"
