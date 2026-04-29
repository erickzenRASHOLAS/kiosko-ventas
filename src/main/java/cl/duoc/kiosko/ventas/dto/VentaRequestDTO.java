package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    //¿no se envia id porque el usuario no envia uno para crear una venta?=
    //Preguntar al profesor para corregir y ver si estoy bien o no
    @NotNull(message = "la fecha y hora no pueden ser nulas")
    private Date fechaHoraVenta;
    @NotNull(message = "el total no puede ser nulo")
    private int total;

    //Permite que puedan mandar una lista de detalles
    //Empty porque es una lista, blank no funciona
    @NotEmpty(message = "la venta debe tener al menos un detalle/producto")
    private List<DetalleVentaRequest> detalles;

}
