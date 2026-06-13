package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Assembler.DetalleVentaModelAssembler;
import cl.duoc.kiosko.ventas.Service.DetalleVentaService;
import cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/v1/detalles_ventas")
@Tag(name="Detalles de Ventas", description = "Operaciones relacionadas con  el detalle de las ventas")
@RequiredArgsConstructor // Inyección por constructor (mejor práctica que @Autowired en campos)
public class DetalleVentaController {
    private final DetalleVentaService detalleVentaService;
    //necesario para HATEOAS
    private final DetalleVentaModelAssembler assembler;

    // La URL POST /api/v1/detalles_ventas/venta/{ventaId}
    //es debido a que es no pueden existir detalles sin ventas
    @PostMapping("/venta/{ventaId}")
    @Operation(summary = "Agregar un detalle venta", description = "Agrega/guarda un detalle de venta, debe existir una venta a la cual esta pueda ser añadida (Obligatorio por la relación en la base de datos)")
    public ResponseEntity<DetalleVentaResponseDTO> agregarDetalleVenta(@PathVariable Long ventaId,@Valid @RequestBody DetalleVentaRequestDTO detalleDTO) {
        // El service se encarga de guardar los datos, aqui se los damos
        DetalleVentaResponseDTO nuevoDetalle = detalleVentaService.saveDetalleVenta(ventaId, detalleDTO);
        // Retornamos el DTO con estado Created y HATEOAS
        return new ResponseEntity<>(assembler.toModel(nuevoDetalle), HttpStatus.CREATED);
    }
    @GetMapping("")
    @Operation(summary = "Listar todos los detalles de ventas", description = "Lista/Muestra todos los detalles de todos los detalles de ventas existentes")
    public ResponseEntity<CollectionModel<DetalleVentaResponseDTO>> listarDetallesVentas() {
        // El service devuelve una lista de DTO
        List<DetalleVentaResponseDTO> detalles = detalleVentaService.listDetalleVenta();
        // Si no hay nada retorna un noContet
        if (detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            List<DetalleVentaResponseDTO> detallesConLinks = detalles.stream()
                    .map(assembler::toModel)//Sale en la guia
                    .toList();

            Link linkColeccion = linkTo(methodOn(DetalleVentaController.class).listarDetallesVentas()).withSelfRel();

            return ResponseEntity.ok(CollectionModel.of(detallesConLinks, linkColeccion));
        }
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar detalle venta por ID", description = "Busca el detalle de una venta por el id del detalle venta (NO por el de la venta)")
    public ResponseEntity<DetalleVentaResponseDTO> buscarDetalleVentaId(@PathVariable Long id) {
        DetalleVentaResponseDTO detalle = detalleVentaService.findDetalleVentaId(id);

        // Si no lo encuentra lanzamos una expecion
        if (detalle == null) {
            throw new NoSuchElementException("No existe el detalle de venta con Id: " + id);
        }else{
            //se aplica el assembler
            return ResponseEntity.ok(assembler.toModel(detalle));
        }
    }
    @PutMapping("/{id}")
    @Operation(summary = "Acutalizar Detalle de una venta", description = "Actualiza el detalle de una venta, se debe poner el id del detalle en la url para cambiarla /{id}")
    public ResponseEntity<DetalleVentaResponseDTO> actualizarDetalleVenta(@PathVariable Long id,@Valid @RequestBody DetalleVentaRequestDTO detalleDTO){
        DetalleVentaResponseDTO actualizado = detalleVentaService.updateDetalleVenta(id, detalleDTO);

        // Igual que antes si no existe, exepcion
        if (actualizado == null) {
            throw new NoSuchElementException("No se puede actualizar. El detalle con ID " + id + " no existe.");
        }else {
            //se aplica el assembler
            return ResponseEntity.ok(assembler.toModel(actualizado));
        }
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un detalle venta de venta", description = "Elimina el detalle de una venta buscándola por su ID (NO por el de la venta)")
    public ResponseEntity<Void> eliminarDetalleVentaId(@PathVariable Long id) {
        //Si no existe salta la expeción
        detalleVentaService.deleteDetalleVenta(id);
        //si NO salta la expecion se borra
        return ResponseEntity.noContent().build();
    }

}
