package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import cl.duoc.kiosko.ventas.dto.DetalleVentaRequest;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import cl.duoc.kiosko.ventas.dto.VentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class VentaService {
    @Autowired
    private VentaRepository ventaRepository;

    private VentaResponseDTO makeToVentaResponseDTO(Venta venta) {
        // Convertimos la lista de Entidades DetalleVenta a lista de DetalleVentaResponseDTO
        List<DetalleVentaResponseDTO> detallesDTO = venta.getDetalles().stream()
                .map(detalle -> new DetalleVentaResponseDTO(
                        detalle.getDetalle_venta_id(),
                        detalle.getProducto_id(),
                        detalle.getCantidad(),
                        detalle.getSubtotal()
                )).toList();
        //Con la lista convertida ahora transformamos Venta a DTO
        return new VentaResponseDTO(
                venta.getVenta_id(),
                venta.getFecha_hora_venta(),
                venta.getTotal(),
                detallesDTO
        );
    }
    public VentaResponseDTO saveVenta(VentaRequestDTO dto) {
        Venta venta = new Venta();
        venta.setFecha_hora_venta(dto.getFecha_hora_venta());
        venta.setTotal(dto.getTotal());

        if (dto.getDetalles() != null) {
            for (DetalleVentaRequest detDTO : dto.getDetalles()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto_id(detDTO.getProductoId());
                detalle.setCantidad(detDTO.getCantidad());
                detalle.setSubtotal(detDTO.getSubtotal());
                // Vinculación bidireccional
                //esto se lo tuve que pedir a la ia
                detalle.setVenta(venta);
                venta.getDetalles().add(detalle);
            }
        }

        Venta guardada = ventaRepository.save(venta);
        return makeToVentaResponseDTO(guardada);
    }

    public List<VentaResponseDTO> listVenta(){

        return ventaRepository.findAll().stream().map(this::makeToVentaResponseDTO).toList();
    }

    public VentaResponseDTO findVentaDTO(Long id) {
        Venta venta = ventaRepository.findById(id).orElse(null);
        //es lo mismo que hacer un if (debo utilizar mas esta forma)
        return (venta != null) ? makeToVentaResponseDTO(venta) : null;
    }



    public VentaResponseDTO updateVenta(Long id, VentaRequestDTO dto) {
        Venta ventaAModificar = ventaRepository.findById(id).orElse(null);
        if (ventaAModificar != null) {
            ventaAModificar.setTotal(dto.getTotal());
            ventaAModificar.setFecha_hora_venta(dto.getFecha_hora_venta());
            //se añaden solo los atributos que se quieran modificar
            Venta actualizada = ventaRepository.save(ventaAModificar);
            return makeToVentaResponseDTO(actualizada);
        }
        return null;
    }
    /// nulos NO Retornan
    public void deleteVenta(Long id) {
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
        }else{
            //si no hay nada expecion
            throw new java.util.NoSuchElementException("No se puede eliminar. La venta con ID " + id + " no existe.");
        }
    }



}
