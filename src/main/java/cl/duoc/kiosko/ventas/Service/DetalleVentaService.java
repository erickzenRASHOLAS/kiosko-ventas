package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.DetalleVentaRepository;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import cl.duoc.kiosko.ventas.dto.DetalleVentaRequest;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DetalleVentaService {
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private VentaRepository ventaRepository;

    //CREA lo que ve el cliente
    private DetalleVentaResponseDTO makeToDetalleResponseDTO(DetalleVenta detalle) {
        log.error("Se formatea el DetalleVenta a DTO");
        return new DetalleVentaResponseDTO(
                detalle.getDetalleVentaId(),
                detalle.getProductoId(),
                detalle.getCantidad(),
                detalle.getSubtotal()
        );
    }
    // Guardar/Crear Detalle (Recibe Request de venta)
    //
    public DetalleVentaResponseDTO saveDetalleVenta(Long id, DetalleVentaRequest dto) {
        log.error("Se inicia la creación de DetalleVenta a DTO");
        //una Validación de si existe más corta
        //esta está hecha con ia para probrar como funciona
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No existe la venta con el ID: " + id));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProductoId(dto.getProductoId());
        detalle.setCantidad(dto.getCantidad());
        detalle.setSubtotal(dto.getSubtotal());
        detalle.setVenta(venta);
        //no se guarda venta ni id venta
        DetalleVenta guardado = detalleVentaRepository.save(detalle);
        return makeToDetalleResponseDTO(guardado);
    }

    public List<DetalleVentaResponseDTO> listDetalleVenta() {
        // Buscamos todas las entidades
        log.error("Se listan todos los detalleVentas");
        List<DetalleVenta> entidades = detalleVentaRepository.findAll();

        // Conversión de la lista a dto
        return entidades.stream()
                .map(this::makeToDetalleResponseDTO) // Se usa el metodo creado antes para transformar
                .collect(Collectors.toList());
    }


    public DetalleVentaResponseDTO findDetalleVentaId(Long id) {
        log.error("Se busca la venta de ID {}", id);
        DetalleVenta detalle = detalleVentaRepository.findById(id).orElse(null);
        return (detalle != null) ? makeToDetalleResponseDTO(detalle) : null;
        //es lo mismo que if (detalle != null) {
        //    return makeToDetalleResponseDTO(detalle); // Si existe el detalle lo convierte a DTO y lo retorna
        //} else {
        //    return null; // Si no existe devuelve null
        //}
    }



    public DetalleVentaResponseDTO updateDetalleVenta(Long id, DetalleVentaRequest dto) {
        log.error("Se actualiza el detalleVenta con ID {}", id);
        // Buscamos la entidad original en la base de datos que queremos cambiar
        DetalleVenta detalleAModificar = detalleVentaRepository.findById(id).orElse(null);

        if (detalleAModificar != null) {
            // Actualizamos solo los campos que vienen en el DTO
            detalleAModificar.setProductoId(dto.getProductoId());
            detalleAModificar.setCantidad(dto.getCantidad());
            detalleAModificar.setSubtotal(dto.getSubtotal());
            //no se usa venta porque el dueño es el mimso
            DetalleVenta actualizado = detalleVentaRepository.save(detalleAModificar);
            // Retornamos un DTO de respuesta
            return makeToDetalleResponseDTO(actualizado);
        }
        // Si no existe, retornamos null (el GlobalExceptionHandler deberia encargarse del resto)
        return null;
    }

    public void deleteDetalleVenta(Long id) {
        log.error("Se elimina el detalleVenta con ID {}", id);
        // verifica que exista algo que borrar
        if (detalleVentaRepository.existsById(id)) {

            detalleVentaRepository.deleteById(id);
        }else {
            throw new java.util.NoSuchElementException("No se puede eliminar, El detalle con ID " + id + " no existe.");
        }
    }


}
