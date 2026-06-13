CREATE TABLE ventas (
                        venta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        fecha_hora_venta TIMESTAMP NOT NULL,
                        total INT NOT NULL
);

CREATE TABLE detalle_venta (
                               detalle_venta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               venta_id BIGINT NOT NULL,
                               producto_id BIGINT NOT NULL,
                               cantidad INT NOT NULL,
                               subtotal INT NOT NULL,
                               CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (venta_id) REFERENCES ventas(venta_id)
);