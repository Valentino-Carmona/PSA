-- Crear tabla de usuarios
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Insertar usuario admin por defecto (password = admin)
-- Se recomienda cambiar en un entorno real. La clave está hasheada con BCrypt
INSERT INTO users (id, first_name, last_name, email, password, role) 
VALUES (gen_random_uuid(), 'Admin', 'PSA', 'admin@psa.com', '$2a$10$tZ9qQ0G3O5PzVq7A2v7rT.3H8X3Y8qQ0G3O5PzVq7A2v7rT.3H8X3', 'ROLE_ADMIN');
