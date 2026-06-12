package cl.duoc.kiosko.ventas.Assembler;

import cl.duoc.kiosko.ventas.Controller.DetalleVentaController;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DetalleVentaModelAssembler implements RepresentationModelAssembler<DetalleVentaResponseDTO, DetalleVentaResponseDTO> {

    @Override
    public DetalleVentaResponseDTO toModel(DetalleVentaResponseDTO detalle) {
        detalle.removeLinks(); // Limpiamos para evitar duplicados

        //Link a sí mismo
        detalle.add(linkTo(methodOn(DetalleVentaController.class).buscarDetalleVentaId(detalle.getDetalleVentaId())).withSelfRel());

        //Link a la colección completa
        detalle.add(linkTo(methodOn(DetalleVentaController.class).listarDetallesVentas()).withRel("todos-los-detalles"));

        //Links de acción (Actualizar y Eliminar)
        detalle.add(linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(detalle.getDetalleVentaId(), null)).withRel("actualizar-detalle"));
        detalle.add(linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVentaId(detalle.getDetalleVentaId())).withRel("eliminar-detalle"));

        //Link al microservicio externo (se debe evaluar su implementación)
        // Esto le dice al frontend dónde ir a buscar la información completa de ese producto
        //detalle.add(org.springframework.hateoas.Link.of("http://localhost:8081/v1/productos/" + detalle.getProductoId()).withRel("ver-producto"));

        return detalle;
    }
}