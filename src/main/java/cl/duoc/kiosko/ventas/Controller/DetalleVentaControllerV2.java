package cl.duoc.kiosko.ventas.Controller;
import cl.duoc.kiosko.ventas.assemblers.DetalleVentaAssembler;
import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@RestController
@RequestMapping("/v2/detalle_ventas")
public class DetalleVentaControllerV2 {
    @Autowired
    private DetalleVentaService detalleVentaService;
    @Autowired
    private DetalleVentaAssembler detalleVentaAssembler;

}
