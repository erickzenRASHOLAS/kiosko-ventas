package cl.duoc.kiosko.ventas.Config; // Ajusta el paquete según tu estructura

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//DATA FAKER HECHO POR IA

@Configuration
@Profile({"dev", "test"})
public class DataFakerConfig {

    @Bean
    CommandLineRunner iniciarDatosFalsos(VentaRepository ventaRepository) {
        return args -> {
            // Verificamos si la base de datos está vacía para no duplicar datos en cada reinicio
            if (ventaRepository.count() == 0) {
                Faker faker = new Faker();
                List<Venta> ventasFalsas = new ArrayList<>();

                System.out.println(" Generando datos de prueba con Datafaker...");

                // Vamos a crear 10 ventas aleatorias
                for (int i = 0; i < 10; i++) {
                    Venta venta = new Venta();
                    // Fecha aleatoria de los últimos 30 días
                    venta.setFechaHoraVenta(faker.date().past(30, TimeUnit.DAYS));

                    int totalVenta = 0;
                    List<DetalleVenta> detalles = new ArrayList<>();

                    // Cada venta tendrá entre 1 y 4 detalles (productos diferentes)
                    int cantidadDeDetalles = faker.number().numberBetween(1, 5);

                    for (int j = 0; j < cantidadDeDetalles; j++) {
                        DetalleVenta detalle = new DetalleVenta();
                        // Simulamos IDs de productos entre el 1 y el 50
                        detalle.setProductoId((long) faker.number().numberBetween(1, 51));
                        // Cantidad comprada entre 1 y 5
                        detalle.setCantidad(faker.number().numberBetween(1, 6));

                        // Simulamos un precio aleatorio para calcular el subtotal
                        int precioFalso = faker.number().numberBetween(1000, 5000);
                        int subtotal = detalle.getCantidad() * precioFalso;
                        detalle.setSubtotal(subtotal);

                        // IMPORTANTE: Unir el detalle con la venta (relación bidireccional)
                        detalle.setVenta(venta);
                        detalles.add(detalle);

                        // Sumamos al total de la factura
                        totalVenta += subtotal;
                    }

                    // Asignamos la lista de detalles y el total calculado a la venta
                    venta.setDetalles(detalles);
                    venta.setTotal(totalVenta);

                    ventasFalsas.add(venta);
                }

                // Guardamos todas las ventas de golpe.
                // Gracias a tu 'CascadeType.ALL', los detalles se guardarán solos.
                ventaRepository.saveAll(ventasFalsas);

                System.out.println("✅ ¡Base de datos poblada exitosamente con 10 ventas!");
            } else {
                System.out.println("👍 La base de datos ya tiene información, omitiendo Datafaker.");
            }
        };
    }
}