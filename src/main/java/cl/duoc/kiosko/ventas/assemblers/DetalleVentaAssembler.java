package cl.duoc.kiosko.ventas.assemblers;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import cl.duoc.kiosko.ventas.Controller.DetalleVentaController;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DetalleVentaAssembler implements RepresentationModelAssembler<DetalleVentaResponseDTO, EntityModel<DetalleVentaResponseDTO>> {

    @Override
    public EntityModel<DetalleVentaResponseDTO> toModel(DetalleVentaResponseDTO detalleVentaDTO) {
        return EntityModel.of(DetalleVentaResponseDTO,
                linkTo(methodOn(DetalleVentaController.class).getCarrer(carrera.getCodigo())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).getAllCarreras()).withRel("carreras"));
    }
}
