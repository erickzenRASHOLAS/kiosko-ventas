package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Service.DetalleVentaService;
import cl.duoc.kiosko.ventas.Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> agregarDetalleVenta(@PathVariable Long ventaId, @RequestBody DetalleVenta detalleVenta) {
        // Buscamos la venta para asegurarnos de que existe
        Venta venta = ventaService.findVentaId(ventaId);
        if (venta == null) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", "No se puede crear el detalle. No existe la venta con el ID: " + ventaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        // Vinculamos el detalle con la venta encontrada
        // Esto se debe hacer porque el JSON no trae la venta por el @JsonIgnore
        detalleVenta.setVenta(venta);
        // Se guarda el Nuevo DetalleVenta
        DetalleVenta nuevoDetalle = detalleVentaService.saveDetalleVenta(detalleVenta);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }
    @GetMapping("")
    public ResponseEntity<List<DetalleVenta>> listarDetallesVentas(){
        List<DetalleVenta> detallesEncontrados= detalleVentaService.listDetalleVenta();
        //Otra forma
        //List<DetalleVenta> detallesEncontrados= detalleVentaService.listDetalleVenta();
        // return ResponseEntity.ok(detallesEncontrados);
        if(detallesEncontrados.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(detallesEncontrados);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDetalleVentaId(@PathVariable Long id){
        DetalleVenta detalle= detalleVentaService.findDetalleVentaId(id);
        if(detalle!=null){
            return ResponseEntity.ok(detalle);
        }else{
            Map<String, String> respuesta= new HashMap<>();
            respuesta.put("mensaje","No existe el detalle de venta con Id: "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVenta) {
        // Buscamos el detalle existente en la BD
        DetalleVenta existente = detalleVentaService.findDetalleVentaId(id);
        if (existente != null) {
            // Le pasamos la Venta original al objeto que llegó de Postman.
            // Como detalleVenta trae la venta en null (por el @JsonIgnore),
            // aquí le devolvemos su "dueño" original.
            detalleVenta.setVenta(existente.getVenta());

            // LUego de esas validaciones realizamos la actualización
            DetalleVenta actualizado = detalleVentaService.updateDetalleVenta(id, detalleVenta);
            return ResponseEntity.ok(actualizado);
        } else {
            // Mensaje en caso de que no exista
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "No se puede actualizar. El detalle con ID " + id + " no existe.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVentaId(@PathVariable Long id){
        detalleVentaService.deleteDetalleVenta(id);
        return ResponseEntity.noContent().build();
    }

}
