package cl.duoc.kiosko.ventas.Service;

import cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import cl.duoc.kiosko.ventas.Model.DetalleVenta;
import cl.duoc.kiosko.ventas.Model.Venta;
import cl.duoc.kiosko.ventas.Repository.DetalleVentaRepository;
import cl.duoc.kiosko.ventas.Repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalleVentaServiceTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    // Se asume que DetalleVentaService necesita buscar la Venta padre
    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private DetalleVentaService detalleVentaService;

    @Test
    void testListDetalleVenta() {
        // Arrange
        DetalleVenta detalle = new DetalleVenta();
        detalle.setDetalleVentaId(1L);
        when(detalleVentaRepository.findAll()).thenReturn(List.of(detalle));

        // Act
        List<DetalleVentaResponseDTO> responses = detalleVentaService.listDetalleVenta();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(detalleVentaRepository, times(1)).findAll();
    }

    @Test
    void testFindDetalleVentaIdExitoso() {
        // Arrange
        Long id = 1L;
        DetalleVenta detalle = new DetalleVenta();
        detalle.setDetalleVentaId(id);
        when(detalleVentaRepository.findById(id)).thenReturn(Optional.of(detalle));

        // Act
        DetalleVentaResponseDTO response = detalleVentaService.findDetalleVentaId(id);

        // Assert
        assertNotNull(response);
        verify(detalleVentaRepository, times(1)).findById(id);
    }

    @Test
    void testSaveDetalleVentaExitoso() {
        // Arrange
        Long ventaId = 10L;
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO();
        request.setCantidad(2);
        request.setSubtotal(5000);

        Venta ventaPadre = new Venta();
        ventaPadre.setVentaId(ventaId);

        DetalleVenta detalleGuardado = new DetalleVenta();
        detalleGuardado.setDetalleVentaId(1L);
        detalleGuardado.setCantidad(2);
        detalleGuardado.setSubtotal(5000);
        detalleGuardado.setVenta(ventaPadre);

        // Mockeamos la búsqueda de la venta padre (esto cubre la lambda$saveDetalleVenta)
        when(ventaRepository.findById(ventaId)).thenReturn(Optional.of(ventaPadre));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleGuardado);

        // Act
        DetalleVentaResponseDTO response = detalleVentaService.saveDetalleVenta(ventaId, request);

        // Assert
        assertNotNull(response);
        verify(ventaRepository, times(1)).findById(ventaId);
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
    }

    @Test
    void testUpdateDetalleVentaExitoso() {
        // Arrange
        Long id = 1L;
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO();
        request.setCantidad(5);

        DetalleVenta detalleViejo = new DetalleVenta();
        detalleViejo.setDetalleVentaId(id);

        DetalleVenta detalleActualizado = new DetalleVenta();
        detalleActualizado.setDetalleVentaId(id);
        detalleActualizado.setCantidad(5);

        when(detalleVentaRepository.findById(id)).thenReturn(Optional.of(detalleViejo));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleActualizado);

        // Act
        DetalleVentaResponseDTO response = detalleVentaService.updateDetalleVenta(id, request);

        // Assert
        assertNotNull(response);
        verify(detalleVentaRepository, times(1)).findById(id);
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
    }

    @Test
    void testDeleteDetalleVentaExitoso() {
        // Arrange
        Long id = 1L;

        // Entrenamos al Mock SOLO para existsById, que es el que tu código realmente usa
        when(detalleVentaRepository.existsById(id)).thenReturn(true);
        doNothing().when(detalleVentaRepository).deleteById(id);

        // Act
        detalleVentaService.deleteDetalleVenta(id);

        // Assert
        // Verificamos que se hayan llamado exactamente estos dos métodos
        verify(detalleVentaRepository, times(1)).existsById(id);
        verify(detalleVentaRepository, times(1)).deleteById(id);
    }
}