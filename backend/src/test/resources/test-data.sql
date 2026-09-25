-- Test data for H2 database tests
INSERT INTO projects (name, type, billing_type, start_date, status, client_id) 
VALUES ('Test Project 1', 'DEVELOPMENT', 'FIXED_PRICE', CURRENT_DATE, 'INITIATED', 1);

INSERT INTO tasks (name, status, estimated_hours, project_id) 
VALUES ('Test Task 1', 'TO_DO', 8, 1); 