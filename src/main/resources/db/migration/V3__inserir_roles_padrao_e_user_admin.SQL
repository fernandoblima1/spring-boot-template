-- Inserir permissões padrão
INSERT INTO permissions (module, action) VALUES 
('auth', 'all'),
('auth', 'read'),
('auth', 'write'),
('auth', 'update'),
('auth', 'delete');

-- Inserir roles padrão
INSERT INTO roles (name) VALUES 
('ADMIN'),
('USER');

-- Associar todas as permissões ao role ADMIN
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r 
CROSS JOIN permissions p 
WHERE r.name = 'ADMIN';

-- Associar apenas permissão de READ ao role USER
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r 
JOIN permissions p ON p.module = 'auth' AND p.action = 'read'
WHERE r.name = 'USER';

-- Criar usuário admin (senha: password)
INSERT INTO users (username, password, email) VALUES 
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@example.com');

-- Associar role ADMIN ao usuário admin
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u 
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin';