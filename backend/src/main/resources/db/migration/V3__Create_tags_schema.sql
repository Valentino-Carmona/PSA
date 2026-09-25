-- Tabla para tags de proyectos
CREATE TABLE project_tags (
    id SERIAL PRIMARY KEY,
    project_id INT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    tag_name VARCHAR(50) NOT NULL,    

    -- Evitar duplicados
    UNIQUE(project_id, tag_name)
);

-- Tabla para tags de tareas
CREATE TABLE task_tags (
    id SERIAL PRIMARY KEY,
    task_id INT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    tag_name VARCHAR(50) NOT NULL,
    
    -- Evitar duplicados
    UNIQUE(task_id, tag_name)
);