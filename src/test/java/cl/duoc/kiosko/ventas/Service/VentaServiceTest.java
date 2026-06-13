package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository; // Burlamos la conexión a la Base de Datos

    @InjectMocks
    private VentaService ventaService; // Inyectamos el mock en tu servicio real

    @Test
    @DisplayName("Debe listar todas las ventas correctamente")
    void testListVenta() {
        // 1. GIVEN / ARRANGE (Preparar)
        Venta ventaMock = new Venta();
        ventaMock.setVentaId(1L);
        ventaMock.setTotal(5000);
        ventaMock.setFechaHoraVenta(new java.util.Date());

        //++++++CRÍTICO: Inicializamos la lista de detalles vacía para que el .stream() no de error
        ventaMock.setDetalles(new ArrayList<>());

        // Le decimos a Mockito: Cuando llamen a findAll(), devuelve esta lista ficticia
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(ventaMock));

        // 2. WHEN / ACT (Actuar)
        // Ejecutamos TU método real
        List<VentaResponseDTO> resultado = ventaService.listVenta();

        // 3. THEN / ASSERT (Afirmar)
        assertNotNull(resultado, "La lista devuelta no debe ser nula");
        assertEquals(1, resultado.size(), "Debe haber exactamente 1 elemento en la lista");
        assertEquals(1L, resultado.get(0).getId(), "El ID de la venta mapeada debe ser 1");

        // Verificamos que el repositorio fue llamado 1 vez
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar una venta por ID exitosamente")
    void testFindVentaDTO() {
        // 1. GIVEN / ARRANGE
        Venta ventaMock = new Venta();
        ventaMock.setVentaId(10L);
        ventaMock.setTotal(15000);
        ventaMock.setDetalles(new ArrayList<>()); // Lista vacía para evitar nulos

        // Simulamos que el repositorio encuentra la venta
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(ventaMock));

        // 2. WHEN / ACT
        VentaResponseDTO resultado = ventaService.findVentaDTO(10L);

        // 3. THEN / ASSERT
        assertNotNull(resultado, "La venta encontrada no debe ser nula");
        assertEquals(10L, resultado.getId(), "El ID de la venta debe coincidir");
        assertEquals(15000, resultado.getTotal(), "El total de la venta debe coincidir");

        verify(ventaRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una venta que no existe")
    void testDeleteVentaNoExiste() {
        // 1. GIVEN / ARRANGE
        // Simulamos que la venta 99 NO existe en la base de datos
        when(ventaRepository.existsById(99L)).thenReturn(false);

        // 2 & 3. WHEN / ACT & THEN / ASSERT (Combinados)
        // Verificamos que al llamar a deleteVenta(99L) se lance TU excepción específica
        Exception exception = assertThrows(java.util.NoSuchElementException.class, () -> {
            ventaService.deleteVenta(99L);
        });

        // Verificamos que el mensaje de error sea el que tú escribiste en el Service
        assertTrue(exception.getMessage().contains("No se puede eliminar. La venta con ID 99 no existe."));

        // Verificamos que verificó si existía, pero que NUNCA intentó borrarlo
        verify(ventaRepository, times(1)).existsById(99L);
        verify(ventaRepository, never()).deleteById(anyLong());
    }
}