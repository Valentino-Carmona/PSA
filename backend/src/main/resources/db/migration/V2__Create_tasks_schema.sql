-- Tabla para tareas
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    ticket_id INTEGER,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TO_DO' CHECK (status IN ('TO_DO', 'IN_PROGRESS', 'DONE')),
    estimated_hours INT NOT NULL CHECK (estimated_hours > 0),
    assigned_resource_id VARCHAR(36) -- ID del recurso asignado (referencia a un ID en sistema externo, no es FK)
);