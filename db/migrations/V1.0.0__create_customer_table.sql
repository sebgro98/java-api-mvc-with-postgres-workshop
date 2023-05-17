CREATE TABLE IF NOT EXISTS Customers (
    id serial PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(1024) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(16)
)

