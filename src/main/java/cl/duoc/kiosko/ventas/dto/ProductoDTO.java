package cl.duoc.kiosko.ventas.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private int precio;
    //solo incluye los campos que interesa leer del otro microservicio
}
