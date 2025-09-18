ALTER TABLE cuentas
ADD COLUMN cliente_id BIGINT,
ADD CONSTRAINT fk_cuenta_cliente
FOREIGN KEY (cliente_id) REFERENCES clientes(id);
