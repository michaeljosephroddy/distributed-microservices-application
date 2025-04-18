CREATE DATABASE IF NOT EXISTS onlinestore;
USE onlinestore;

CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_orders INT DEFAULT 0
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

INSERT INTO customers (id, name, email, address, created_at, total_orders) VALUES
(1, 'John Doe', 'johndoe@email.com', 'TUS Athlone', '2025-02-10 10:00:00', 3),
(2, 'Ethan Carter', 'ethan.carter@email.com', 'Waterford, Ireland', '2025-02-20 10:00:00', 3),
(3, 'Mia Davis', 'mia.davis@email.com', 'Kilkenny, Ireland', '2025-02-21 11:00:00', 4);

-- Insert mock orders with varying customer IDs, products, and timestamps
INSERT INTO orders (customer_id, product, quantity, created_at) VALUES
-- Orders for John Doe
(1, 'External Monitor', 1, '2025-02-23 09:00:00'),
(1, 'Laptop Stand', 2, '2025-02-24 10:00:00'),
(1, 'Wireless Mouse', 1, '2025-02-25 11:00:00'),
(1, 'USB-C Hub', 1, '2025-02-26 12:00:00'),
(1, 'Mechanical Keyboard', 1, '2025-02-27 13:00:00'),
(1, 'Webcam', 1, '2025-02-28 14:00:00'),
(1, 'Headphones', 1, '2025-03-01 15:00:00'),
(1, 'Laptop Bag', 1, '2025-03-02 16:00:00'),
(1, 'Portable SSD', 1, '2025-03-03 17:00:00'),
(1, 'Smartphone Stand', 1, '2025-03-04 18:00:00'),
(1, 'Wireless Charger', 1, '2025-03-05 19:00:00'),
(1, 'Bluetooth Speaker', 1, '2025-03-06 20:00:00'),
(1, 'Smartwatch', 1, '2025-03-07 21:00:00'),
(1, 'Fitness Tracker', 1, '2025-03-08 22:00:00'),
(1, 'VR Headset', 1, '2025-03-09 23:00:00'),
(1, 'Gaming Mouse', 1, '2025-03-10 00:00:00'),
(1, 'Mechanical Gaming Keyboard', 1, '2025-03-11 01:00:00'),
(1, 'Gaming Headset', 1, '2025-03-12 02:00:00'),
(1, 'Gaming Chair', 1, '2025-03-13 03:00:00'),
(1, 'Gaming Desk', 1, '2025-03-14 04:00:00'),

-- Orders for Ethan Carter
(2, 'Smart Thermostat', 1, '2025-03-01 09:00:00'),
(2, 'Robot Vacuum Cleaner', 1, '2025-03-01 10:00:00'),
(2, 'Smart Door Lock', 1, '2025-03-01 11:00:00'),

-- Orders for Mia Davis
(3, 'Electric Scooter', 1, '2025-03-02 12:00:00'),
(3, 'Noise-Cancelling Headphones', 1, '2025-03-02 13:00:00'),
(3, 'Smart Glasses', 1, '2025-03-02 14:00:00'),
(3, 'Portable Projector', 1, '2025-03-02 15:00:00');