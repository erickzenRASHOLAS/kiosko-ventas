package cl.duoc.kiosko.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Crucial para que Lombok y HATEOAS no choquen (tuve que poner esto porque me crasheaba)
public class VentaResponseDTO extends RepresentationModel<VentaResponseDTO> {
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
