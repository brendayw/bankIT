ALTER TABLE clientes ADD COLUMN user_id BIGINT;
ALTER TABLE clientes ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES usuarios(id);