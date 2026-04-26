package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    //¿si no pueden ser nulos en el model, el DTO tampoco?
    //Preguntar al profesor para corregir y ver si estoy bien o no
    @NotNull
    private Date fecha_hora_venta;
    @NotNull
    private int total;
}
