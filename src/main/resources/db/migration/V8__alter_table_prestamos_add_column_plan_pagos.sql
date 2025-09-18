ALTER TABLE prestamos ADD COLUMN plan_pagos_id BIGINT;
ALTER TABLE prestamos ADD CONSTRAINT fk_plan_pago FOREIGN KEY (plan_pagos_id) REFERENCES planes_pago(id);
