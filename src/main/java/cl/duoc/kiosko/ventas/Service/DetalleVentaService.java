package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.DetalleVentaRepository;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor // Inyección por constructor (mejor práctica que @Autowired en campos)
public class DetalleVentaService {
    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;
    private final WebClient webClient;

    // Recalcula el total de la venta como la suma de los subtotales de sus detalles
    // Se llama cada vez que un detalle cambia para que el total NUNCA quede desactualizado
    private void recalcularTotalVenta(Venta venta) {
        int total = venta.getDetalles().stream()
                .mapToInt(DetalleVenta::getSubtotal)
                .sum();
        venta.setTotal(total);
        ventaRepository.save(venta);
    }

    //CREA lo que ve el cliente
    private DetalleVentaResponseDTO makeToDetalleResponseDTO(DetalleVenta detalle) {
        log.info("Se formatea el DetalleVenta a DTO");
        return new DetalleVentaResponseDTO(
                detalle.getDetalleVentaId(),
                detalle.getProductoId(),
                detalle.getCantidad(),
                detalle.getSubtotal()
        );
    }
    // Guardar/Crear Detalle (Recibe Request de venta)
    //
    public DetalleVentaResponseDTO saveDetalleVenta(Long id, DetalleVentaRequestDTO dto) {
        log.info("Se inicia la creación de DetalleVenta a DTO");
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
        // Al agregar un detalle nuevo, el total de la venta debe actualizarse
        // (la lista lazy de la venta se carga recién aquí, así que ya incluye el detalle insertado)
        recalcularTotalVenta(venta);
        return makeToDetalleResponseDTO(guardado);
    }

    public List<DetalleVentaResponseDTO> listDetalleVenta() {
        // Buscamos todas las entidades
        log.info("Se listan todos los detalleVentas");
        List<DetalleVenta> entidades = detalleVentaRepository.findAll();

        // Conversión de la lista a dto
        return entidades.stream()
                .map(this::makeToDetalleResponseDTO) // Se usa el metodo creado antes para transformar
                .collect(Collectors.toList());
    }


    public DetalleVentaResponseDTO findDetalleVentaId(Long id) {
        log.info("Se busca la venta de ID {}", id);
        DetalleVenta detalle = detalleVentaRepository.findById(id).orElse(null);
        return (detalle != null) ? makeToDetalleResponseDTO(detalle) : null;
        //es lo mismo que if (detalle != null) {
        //    return makeToDetalleResponseDTO(detalle); // Si existe el detalle lo convierte a DTO y lo retorna
        //} else {
        //    return null; // Si no existe devuelve null
        //}
    }



    public DetalleVentaResponseDTO updateDetalleVenta(Long id, DetalleVentaRequestDTO dto) {
        log.info("Se actualiza el detalleVenta con ID {}", id);
        // Buscamos la entidad original en la base de datos que queremos cambiar
        DetalleVenta detalleAModificar = detalleVentaRepository.findById(id).orElse(null);

        if (detalleAModificar != null) {
            // Actualizamos solo los campos que vienen en el DTO
            detalleAModificar.setProductoId(dto.getProductoId());
            detalleAModificar.setCantidad(dto.getCantidad());
            detalleAModificar.setSubtotal(dto.getSubtotal());
            //no se usa venta porque el dueño es el mimso
            DetalleVenta actualizado = detalleVentaRepository.save(detalleAModificar);
            // Si cambió el subtotal, el total de la venta padre debe reflejarlo
            recalcularTotalVenta(actualizado.getVenta());
            // Retornamos un DTO de respuesta
            return makeToDetalleResponseDTO(actualizado);
        }
        // Si no existe, retornamos null (el GlobalExceptionHandler deberia encargarse del resto)
        return null;
    }

    public void deleteDetalleVenta(Long id) {
        log.info("Se elimina el detalleVenta con ID {}", id);
        // verifica que exista algo que borrar (si no, excepción → 404)
        DetalleVenta detalle = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se puede eliminar, El detalle con ID " + id + " no existe."));

        Venta venta = detalle.getVenta();
        // Se quita de la lista de la venta y orphanRemoval lo borra de la BD
        // (se compara por ID para no usar equals(), que con la relación bidireccional se cae en bucle)
        venta.getDetalles().removeIf(d -> d.getDetalleVentaId().equals(id));
        // Y el total de la venta queda actualizado sin ese subtotal
        recalcularTotalVenta(venta);
    }

}
