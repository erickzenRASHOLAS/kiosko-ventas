package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Service.DetalleVentaService;
import cl.duoc.kiosko.ventas.Service.VentaService;
import cl.duoc.kiosko.ventas.dto.DetalleVentaRequest;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/detalles_ventas")
public class DetalleVentaController {
    @Autowired
    private DetalleVentaService detalleVentaService;
    //NECESARIO PARA ENCONTRAR LA VENTA
    @Autowired
    private VentaService ventaService;

    // La URL POST /api/v1/detalles_ventas/venta/{ventaId}
    //es debido a que es no pueden existir detalles sin ventas
    @PostMapping("/venta/{ventaId}")
    public ResponseEntity<DetalleVentaResponseDTO> agregarDetalleVenta(@PathVariable Long ventaId, @RequestBody DetalleVentaRequest detalleDTO) {
        // El service se encarga de guardar los datos, aqui se los damos
        DetalleVentaResponseDTO nuevoDetalle = detalleVentaService.saveDetalleVenta(ventaId, detalleDTO);
        // Retornamos el DTO con estado Created
        return new ResponseEntity<>(nuevoDetalle, HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetallesVentas() {
        // El service devuelve una lista de DTO
        List<DetalleVentaResponseDTO> detalles = detalleVentaService.listDetalleVenta();
        // Si no hay nada retorna un noContet
        if (detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(detalles);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaResponseDTO> buscarDetalleVentaId(@PathVariable Long id) {
        DetalleVentaResponseDTO detalle = detalleVentaService.findDetalleVentaId(id);

        // Si no lo encuentra lanzamos una expecion
        if (detalle == null) {
            throw new NoSuchElementException("No existe el detalle de venta con Id: " + id);
        }else{
            return ResponseEntity.ok(detalle);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaResponseDTO> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVentaRequest detalleDTO){
        DetalleVentaResponseDTO actualizado = detalleVentaService.updateDetalleVenta(id, detalleDTO);

        // Igual que antes si no existe, exepcion
        if (actualizado == null) {
            throw new NoSuchElementException("No se puede actualizar. El detalle con ID " + id + " no existe.");
        }else {
            return ResponseEntity.ok(actualizado);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVentaId(@PathVariable Long id) {
        //Si no existe salta la expeción
        detalleVentaService.deleteDetalleVenta(id);
        //si NO salta la expecion se borra
        return ResponseEntity.noContent().build();
    }

}
