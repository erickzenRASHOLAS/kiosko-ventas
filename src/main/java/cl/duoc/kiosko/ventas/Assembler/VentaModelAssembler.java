package cl.duoc.kiosko.ventas.Assembler;

import cl.duoc.kiosko.ventas.Controller.VentaController;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component // Importante para poder inyectarlo en el controlador
public class VentaModelAssembler implements RepresentationModelAssembler<VentaResponseDTO, VentaResponseDTO> {

    @Override
    public VentaResponseDTO toModel(VentaResponseDTO venta) {
        // Aquí centralizamos TODOS los links de una Venta
        // Limpiamos la lista de links en caso de que ya tenga para evitar duplicados
        venta.removeLinks();

        venta.add(linkTo(methodOn(VentaController.class).buscarVentaId(venta.getId())).withSelfRel());
        venta.add(linkTo(methodOn(VentaController.class).listarVentas()).withRel("todas-las-ventas"));
        venta.add(linkTo(methodOn(VentaController.class).actualizarVenta(venta.getId(), null)).withRel("actualizar-venta"));
        venta.add(linkTo(methodOn(VentaController.class).eliminarVentaId(venta.getId())).withRel("eliminar-venta"));

        return venta;
    }
}