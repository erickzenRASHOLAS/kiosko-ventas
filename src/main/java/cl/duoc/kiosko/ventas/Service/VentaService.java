package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class VentaService {
    @Autowired
    private VentaRepository ventaRepository;

    public List<Venta> listVenta(){
        return ventaRepository.findAll();
    }

    //Justificar el metodo
    public Venta findVentaId(Long id){
        //El metodo que usa JPA devuelve un Optional el cual basicamente
        //si NO encuentra lo que buscamos es null
        // y si lo encuentra seria lo que buscamos (Objeto Ventas en este caso)
        //Esto para mostar un 404 en vez 500 que seria lo correcto en este caso
        return ventaRepository.findById(id).orElse(null);
    }

    public Venta saveVenta(Venta ven){
        //Sirve para guardar y actualizar pero se debe utilizar de manera distinta
        //Este codigo es para guardar una venta con todos los Detalles de Venta que tenga
        if(ven.getDetalles()!= null){
            //Recorremos los detalles con los que venga y le asignamos esta venta como su "dueño"
            //Esto es asi porque es una relación uno a mucho
            for (DetalleVenta detalle : ven.getDetalles()){
                detalle.setVenta(ven);
            }
        }
        //por la relación hecha con cascade all
        return ventaRepository.save(ven);
    }

    public Venta updateVenta(Long id,Venta ven){
        Venta venta_a_modificar= ventaRepository.findById(id).orElse(null);
        if(venta_a_modificar!=null){
            venta_a_modificar.setTotal(ven.getTotal());
            venta_a_modificar.setFecha_hora_venta(ven.getFecha_hora_venta());

            return ventaRepository.save(venta_a_modificar);
        }else{
            return null;
        }
    }
    /// nulos NO Retornan
    public void deleteVenta(Long id){
        ventaRepository.deleteById(id);
    }



}
