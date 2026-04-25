package cl.duoc.kiosko.ventas.Repository;

import cl.duoc.kiosko.ventas.Model.Venta;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


@Repository
public interface VentaRepository extends  JpaRepository<Venta, Long>{
    //Se deben agregar cosas para funciones especificas que no  esten en el JPA
    //Ejemplo sugerido por IntelliJ
    //List<Venta> findAllByFechaHoraVentaBetween(Date inicio, Date fin);

}
