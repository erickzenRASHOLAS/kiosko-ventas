package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaResponseDTO {
    @NotNull
    private Long detalleVentaId;
    @NotNull
    private Long productoId;
    @NotNull
    @Min(value = 0,message = "la cantidad no puede ser negativo")
    private int cantidad;
    @NotNull
    @Min(value = 0,message = "el subtotal no puede ser negativo")
    private int subtotal;
}
