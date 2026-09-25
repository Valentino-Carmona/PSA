-- Tabla para proyectos
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    client_id INTEGER, -- ID del cliente del sistema externo (NO es FK)
    type VARCHAR(50) NOT NULL CHECK (type IN ('DEVELOPMENT', 'IMPLEMENTATION')),
    billing_type VARCHAR(50) NOT NULL CHECK (billing_type IN ('TIME_AND_MATERIAL', 'FIXED_PRICE')),
    start_date DATE NOT NULL,
    end_date DATE,
    estimated_hours INT CHECK (estimated_hours >= 0),
    status VARCHAR(50) NOT NULL DEFAULT 'INITIATED' CHECK (status IN ('INITIATED', 'IN_PROGRESS', 'TRANSITION')),
    leader_id VARCHAR(36), -- ID del líder del proyecto del sistema externo de recursos (NO es FK)
    
    -- Constraints
    CONSTRAINT chk_project_dates CHECK (end_date IS NULL OR end_date >= start_date)
);