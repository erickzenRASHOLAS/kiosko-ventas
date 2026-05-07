package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaRequest {
    //NO VA ID PORQUE EL USUARIO NO PONE ID AL CREARLO
    @NotNull(message = "Debe existir un id producto")
    private Long productoId;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;
    @NotNull
    @Min(value = 0, message = "El subtotal no puede ser negativo")
    private int subtotal;

}
