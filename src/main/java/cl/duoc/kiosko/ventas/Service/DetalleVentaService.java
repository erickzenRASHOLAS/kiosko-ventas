package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaService {
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    public List<DetalleVenta> listDetalleVenta(){
        return detalleVentaRepository.findAll();
    }
    public DetalleVenta findDetalleVentaId(Long id){
        return detalleVentaRepository.findById(id).orElse(null);
    }
    public DetalleVenta saveDetalleVenta(DetalleVenta ven){
        //Guarda
        return detalleVentaRepository.save(ven);
    }
    public DetalleVenta updateDetalleVenta(Long id, DetalleVenta ven) {
        DetalleVenta detalleAModificar = detalleVentaRepository.findById(id).orElse(null);
        if (detalleAModificar != null) {
            // Solo cambiamos la venta si el objeto 'ven' trae una
            // (no es lo común en un update)
            if (ven.getVenta() != null) {
                //es una doble validación
                detalleAModificar.setVenta(ven.getVenta());
            }

            detalleAModificar.setProducto_id(ven.getProducto_id());
            detalleAModificar.setCantidad(ven.getCantidad());
            detalleAModificar.setSubtotal(ven.getSubtotal());

            return detalleVentaRepository.save(detalleAModificar);
        }
        return null;
    }
    public void deleteDetalleVenta(Long id){
        //Busca que exista el detalle venta y si existe lo elimina
        if(detalleVentaRepository.existsById(id)){
            detalleVentaRepository.deleteById(id);
        }
    }


}
