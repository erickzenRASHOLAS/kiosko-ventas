package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Long id;
    @NotNull
    private Date fechaHoraVenta;
    @NotNull
    @Min(value = 0,message = "el total no puede ser negativo")
    private int total;
    // Permite ver los detalles de la venta asociados en JSON
    @NotNull
    private List<DetalleVentaResponseDTO> detalles;
}
