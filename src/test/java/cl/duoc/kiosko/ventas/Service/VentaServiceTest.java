package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import cl.duoc.kiosko.ventas.dto.VentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

    @Test
    @DisplayName("Debe retornar null cuando busca una venta que no existe")
    void testFindVentaDTO_NoExiste_RetornaNull() {
        // Usa directamente when y Optional de java.util
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        VentaResponseDTO resultado = ventaService.findVentaDTO(999L);

        assertNull(resultado);
        verify(ventaRepository, times(1)).findById(999L); // Agrega esta verificación
    }

    @Test
    void testUpdateVenta_NoExiste_RetornaNull() {
        org.mockito.Mockito.when(ventaRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        VentaResponseDTO resultado = ventaService.updateVenta(999L, new VentaRequestDTO());

        assertNull(resultado);
    }

    @Test
    void testDeleteVenta_NoExiste_LanzaNoSuchElementException() {
        // Simulamos que la venta NO existe en la base de datos
        org.mockito.Mockito.when(ventaRepository.existsById(999L)).thenReturn(false);

        // Verificamos que salte tu excepción personalizada del 'else'
        assertThrows(java.util.NoSuchElementException.class, () -> {
            ventaService.deleteVenta(999L);
        });
    }

    @Test
    void testSaveVentaExitoso() {
        // 1. Arrange (Preparar datos con un detalle para obligar a la Lambda a ejecutarse)
        VentaRequestDTO request = new VentaRequestDTO();
        request.setTotal(7000);

        // Agregamos un detalle al request para activar el flujo del stream
        cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO detalleReq = new cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO();
        request.setDetalles(java.util.List.of(detalleReq));

        Venta ventaGuardada = new Venta();
        ventaGuardada.setVentaId(10L);
        ventaGuardada.setTotal(7000);

        // Agregamos el detalle correspondiente en la entidad simulada
        cl.duoc.kiosko.ventas.Model.DetalleVenta detalleEntidad = new cl.duoc.kiosko.ventas.Model.DetalleVenta();
        ventaGuardada.setDetalles(java.util.List.of(detalleEntidad));

        // Simulamos el comportamiento del repositorio
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardada);

        // 2. Act
        VentaResponseDTO response = ventaService.saveVenta(request);

        // 3. Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(7000, response.getTotal());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }
    @Test
    void testUpdateVentaExitoso() {
        // 1. Arrange (Preparar)
        Long id = 5L;
        VentaRequestDTO requestActualizacion = new VentaRequestDTO();
        requestActualizacion.setTotal(12000);
        requestActualizacion.setDetalles(new ArrayList<>());

        // Esta es la venta vieja que está en la base de datos
        Venta ventaVieja = new Venta();
        ventaVieja.setVentaId(id);
        ventaVieja.setTotal(5000);

        // Esta es la venta después de guardar los cambios
        Venta ventaGuardada = new Venta();
        ventaGuardada.setVentaId(id);
        ventaGuardada.setTotal(12000);
        ventaGuardada.setDetalles(new ArrayList<>());

        // Le decimos al mock que encuentre la vieja, y luego devuelva la nueva al guardar
        when(ventaRepository.findById(id)).thenReturn(java.util.Optional.of(ventaVieja));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardada);

        // 2. Act (Actuar)
        VentaResponseDTO response = ventaService.updateVenta(id, requestActualizacion);

        // 3. Assert (Afirmar)
        assertNotNull(response);
        assertEquals(12000, response.getTotal(), "El total debió actualizarse a 12000");

        // Verificamos que se buscó y luego se guardó
        verify(ventaRepository, times(1)).findById(id);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

}