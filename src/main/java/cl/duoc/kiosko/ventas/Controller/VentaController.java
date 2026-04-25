package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping("")
    public ResponseEntity<Venta> agregarVenta(@RequestBody Venta ven) {
        Venta nuevaVenta = ventaService.saveVenta(ven);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    //para enviar mensaje personalizado se debe poner un ? dentro del response entity
    //debe retornar ResponseEntity.status(HTTPSTATUS:(estatus correspondiente) ).build(respuesta)
    @GetMapping("")
    public ResponseEntity<List<Venta> > listarVentas(){
        //Otra forma de hacer el mismo metood
        //List<Venta> ventasEncontradas= ventaService.listVentas();
        // return ResponseEntity.ok(ventasEncontradas);

        List<Venta> ventasEcontradas = ventaService.listVenta();
        if(ventasEcontradas.isEmpty()){
            //Map<String, String> respuesta = new HashMap<>();
            //respuesta.put("mensaje", "No se encuentran ventas\nDebe agregar una o mas ventas");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ventasEcontradas);
    }

    //Justificar
    @GetMapping("/{id}")
    //el ? se utiliza en este caso para poder enviar un mensaje y mostrar la venta si la encuentra
    public ResponseEntity<?> buscarVentaId(@PathVariable Long id){
        Venta ventaEncontrada = ventaService.findVentaId(id);
        if(ventaEncontrada != null){
            return  ResponseEntity.ok(ventaEncontrada);
        }else{
            // Map se utiliza para enviar respuesta en formato json
            //Map funciona como diccionario importante recordar
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Venta no encontrada | Id buscado: "+id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarVenta(@PathVariable Long id, @RequestBody Venta ven) {
        Venta ventaActualizada = ventaService.updateVenta(id, ven);
        if (ventaActualizada != null) {
            return ResponseEntity.ok(ventaActualizada);
        } else {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "No se puede actualizar Venta no encontrada con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>eliminarVentaId(@PathVariable Long id){
        ventaService.deleteVenta(id);

        return ResponseEntity.noContent().build();
    }

}
