package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Service.VentaService;
import cl.duoc.kiosko.ventas.dto.VentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping("")
    public ResponseEntity<VentaResponseDTO> agregarVenta(@RequestBody VentaRequestDTO ventaDTO) {
        // El service ahora recibe un RequestDTO y devuelve un ResponseDTO
        VentaResponseDTO nuevaVenta = ventaService.saveVenta(ventaDTO);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    // Listado de todas las ventas
    @GetMapping("")
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        List<VentaResponseDTO> ventas = ventaService.listVenta();
        if (ventas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(ventas);
        }
    }

    // Busca venta por id
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscarVentaId(@PathVariable Long id) {
        VentaResponseDTO venta = ventaService.findVentaDTO(id);

        if (venta == null) {
            // Ahora no es necesario el map para el mensaje gracias al global handler expection
            throw new NoSuchElementException("Venta no encontrada | Id buscado: " + id);
        }
        return ResponseEntity.ok(venta);
    }

    // Actualiza la venta
    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> actualizarVenta(@PathVariable Long id, @RequestBody VentaRequestDTO ventaDTO) {
        VentaResponseDTO actualizada = ventaService.updateVenta(id, ventaDTO);
        if (actualizada == null) {
            throw new NoSuchElementException("No se puede actualizar. Venta no encontrada con ID: " + id);
        }else{
            return ResponseEntity.ok(actualizada);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVentaId(@PathVariable Long id) {
        // El service ya valida si existe o lanza excepción
        ventaService.deleteVenta(id);
        //si no hay exepcion esta se elimina
        return ResponseEntity.noContent().build();
    }
}
