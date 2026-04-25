package cl.duoc.kiosko.ventas.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@Table(name="detalle_venta")
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_venta_id")
    private Long detalle_venta_id;
    // Relación ManyToOne hacia Venta (Esta es la FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnore // Corta el bucle infinito al responder JSON en Postman
    @ToString.Exclude // Corta el bucle infinito interno de Lombok
    private Venta venta;
    @Column(name = "producto_id")
    @NotNull
    private Long producto_id;
    @Column(name="cantidad")
    @NotNull
    private int cantidad;
    @Column(name="subtotal")
    @NotNull
    private int subtotal;

}
