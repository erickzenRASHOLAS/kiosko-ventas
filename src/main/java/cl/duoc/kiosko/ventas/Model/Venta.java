package cl.duoc.kiosko.ventas.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long venta_id;
    @Column(name = "fecha_hora_venta")
    @NotNull
    private Date fecha_hora_venta;
    @Column(name = "total")
    @NotNull
    private int total;
    // Relación OneToMany hacia DetalleVenta con Cascada ALL
    //orphanRemoval sirve para que si eliminamos un detalleVenta de la DB este se elimine "fisicamente"
    //en otras palabras manda una sentencia SQL con el delete
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

}
