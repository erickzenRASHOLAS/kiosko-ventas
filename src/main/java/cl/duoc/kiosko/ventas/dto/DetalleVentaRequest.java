package cl.duoc.kiosko.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaRequest {
    //NO VA ID PORQUE EL USUARIO NO PONE ID AL CREARLO
    private Long productoId;
    private int cantidad;
    private int subtotal;

}
