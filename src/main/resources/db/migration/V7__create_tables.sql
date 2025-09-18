CREATE TABLE cuotas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_number INT NOT NULL,
    payment_amount DOUBLE NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    payment_date DATE,
    loan_id BIGINT NOT NULL,
    CONSTRAINT fk_cuota_loan FOREIGN KEY (loan_id) REFERENCES prestamos(id)
);

CREATE TABLE planes_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    installments INT,
    interest_rate DOUBLE,
    fixed_amount BOOLEAN
);