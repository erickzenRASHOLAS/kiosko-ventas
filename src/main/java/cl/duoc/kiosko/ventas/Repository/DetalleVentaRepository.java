package cl.duoc.kiosko.ventas.Repository;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

}
