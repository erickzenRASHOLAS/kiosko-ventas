package cl.duoc.kiosko.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaResponseDTO {
    private Long detalle_venta_id;
    private Long productoId;
    private int cantidad;
    private int subtotal;
}
