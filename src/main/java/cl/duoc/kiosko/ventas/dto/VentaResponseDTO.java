package cl.duoc.kiosko.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    //¿Aqui si se envia porque el postman necesita saber el número de venta creado?
    private Long id;

    private Date fechaHoraVenta;

    private int total;
    // Permite ver los detalles de la venta asociados en JSON
    private List<DetalleVentaResponseDTO> detalles;
}
