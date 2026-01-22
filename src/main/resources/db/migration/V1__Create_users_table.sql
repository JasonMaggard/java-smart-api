-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

-- Create unique index on email
CREATE UNIQUE INDEX idx_users_email ON users(email);
