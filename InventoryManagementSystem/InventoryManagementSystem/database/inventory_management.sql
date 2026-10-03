CREATE DATABASE IF NOT EXISTS inventory_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE inventory_management;

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100) NULL,
    phone CHAR(10) NOT NULL UNIQUE,
    email VARCHAR(150) NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (phone REGEXP '^[6-9][0-9]{9}$')
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(120) NOT NULL,
    sku VARCHAR(40) NOT NULL UNIQUE,
    category VARCHAR(80) NOT NULL,
    supplier_id INT NOT NULL,
    purchase_price DECIMAL(12,2) NOT NULL,
    selling_price DECIMAL(12,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 5,
    unit VARCHAR(30) NOT NULL DEFAULT 'piece',
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (purchase_price >= 0),
    CHECK (selling_price > 0),
    CHECK (stock_quantity >= 0),
    CHECK (reorder_level >= 0),
    INDEX idx_products_name (product_name),
    INDEX idx_products_category (category),
    INDEX idx_products_stock (stock_quantity),
    INDEX idx_products_supplier (supplier_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sales (
    sale_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    customer_name VARCHAR(120) NULL,
    customer_phone CHAR(10) NULL,
    subtotal DECIMAL(14,2) NOT NULL,
    discount_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(14,2) NOT NULL,
    payment_method ENUM('CASH','UPI','CARD','OTHER') NOT NULL,
    sale_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('COMPLETED','CANCELLED') NOT NULL DEFAULT 'COMPLETED',
    CHECK (subtotal >= 0),
    CHECK (discount_amount >= 0),
    CHECK (total_amount >= 0),
    CHECK (discount_amount <= subtotal),
    INDEX idx_sales_date_status (sale_date, status),
    INDEX idx_sales_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    purchase_price_at_sale DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_items_sale FOREIGN KEY (sale_id)
        REFERENCES sales(sale_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id)
        REFERENCES products(product_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (quantity > 0),
    CHECK (unit_price > 0),
    CHECK (purchase_price_at_sale >= 0),
    CHECK (line_total >= 0),
    UNIQUE KEY uq_sale_product (sale_id, product_id),
    INDEX idx_items_product (product_id),
    INDEX idx_items_sale (sale_id)
) ENGINE=InnoDB;

-- OPTIONAL DEMO DATA. Run only when these unique values do not already exist.
INSERT IGNORE INTO suppliers
(supplier_name, contact_person, phone, email, address)
VALUES
('Tech Supplies', 'Ravi Kumar', '9876543210', 'tech@example.com', 'Chennai'),
('City Wholesale', 'Priya', '9876543211', 'city@example.com', 'Bengaluru'),
('Fresh Goods', 'Arun', '9876543212', 'fresh@example.com', 'Coimbatore');

INSERT IGNORE INTO products
(product_name, sku, category, supplier_id, purchase_price, selling_price,
 stock_quantity, reorder_level, unit)
SELECT 'Wireless Mouse', 'ELEC-101', 'Electronics', supplier_id, 350.00, 499.00, 25, 5, 'piece'
FROM suppliers WHERE phone='9876543210';

INSERT IGNORE INTO products
(product_name, sku, category, supplier_id, purchase_price, selling_price,
 stock_quantity, reorder_level, unit)
SELECT 'Keyboard', 'ELEC-102', 'Electronics', supplier_id, 550.00, 799.00, 15, 5, 'piece'
FROM suppliers WHERE phone='9876543210';

INSERT IGNORE INTO products
(product_name, sku, category, supplier_id, purchase_price, selling_price,
 stock_quantity, reorder_level, unit)
SELECT 'Notebook', 'STAT-101', 'Stationery', supplier_id, 30.00, 50.00, 100, 20, 'piece'
FROM suppliers WHERE phone='9876543211';

INSERT IGNORE INTO products
(product_name, sku, category, supplier_id, purchase_price, selling_price,
 stock_quantity, reorder_level, unit)
SELECT 'Water Bottle', 'HOME-101', 'Home', supplier_id, 100.00, 149.00, 20, 5, 'piece'
FROM suppliers WHERE phone='9876543212';
