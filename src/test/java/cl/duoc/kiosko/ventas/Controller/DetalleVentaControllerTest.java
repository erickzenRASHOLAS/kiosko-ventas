package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.DetalleVentaResponseDTO;
import cl.duoc.kiosko.ventas.Service.DetalleVentaService;
import cl.duoc.kiosko.ventas.Assembler.DetalleVentaModelAssembler; // <-- ¡Importante!
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalleVentaControllerTest {

    @Mock
    private DetalleVentaService detalleVentaService;

    // 1️⃣ ¡AQUÍ ESTÁ LA MAGIA! Le inyectamos el Assembler de mentira para que no dé NullPointer
    @Mock
    private DetalleVentaModelAssembler assembler;

    @InjectMocks
    private DetalleVentaController detalleVentaController;

    @Test
    void testListDetalleVenta() {
        DetalleVentaResponseDTO responseDTO = new DetalleVentaResponseDTO();
        when(detalleVentaService.listDetalleVenta()).thenReturn(List.of(responseDTO));

        // Act
        ResponseEntity<CollectionModel<DetalleVentaResponseDTO>> response = detalleVentaController.listarDetallesVentas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detalleVentaService, times(1)).listDetalleVenta();
    }

    @Test
    void testFindDetalleVentaId() {
        Long id = 1L;
        DetalleVentaResponseDTO responseDTO = new DetalleVentaResponseDTO();
        when(detalleVentaService.findDetalleVentaId(id)).thenReturn(responseDTO);

        // Act (Ajusta el nombre del método si en tu controlador se llama distinto a buscarDetalleVenta)
        ResponseEntity<?> response = detalleVentaController.buscarDetalleVentaId(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detalleVentaService, times(1)).findDetalleVentaId(id);
    }
    @Test
    void testSaveDetalleVenta() {
        Long ventaId = 10L;
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO();
        DetalleVentaResponseDTO responseDTO = new DetalleVentaResponseDTO();

        when(detalleVentaService.saveDetalleVenta(eq(ventaId), any(DetalleVentaRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<?> response = detalleVentaController.agregarDetalleVenta(ventaId, request);

        // Assert
        assertNotNull(response);
        // ¡AQUÍ ESTÁ EL CAMBIO! Ahora esperamos un 201 CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(detalleVentaService, times(1)).saveDetalleVenta(eq(ventaId), any(DetalleVentaRequestDTO.class));
    }

    @Test
    void testUpdateDetalleVenta() {
        Long id = 1L;
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO();
        DetalleVentaResponseDTO responseDTO = new DetalleVentaResponseDTO();

        when(detalleVentaService.updateDetalleVenta(eq(id), any(DetalleVentaRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<?> response = detalleVentaController.actualizarDetalleVenta(id, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(detalleVentaService, times(1)).updateDetalleVenta(eq(id), any(DetalleVentaRequestDTO.class));
    }

    @Test
    void testDeleteDetalleVenta() {
        Long id = 1L;
        doNothing().when(detalleVentaService).deleteDetalleVenta(id);

        // Act
        ResponseEntity<Void> response = detalleVentaController.eliminarDetalleVentaId(id);

        // Assert
        assertNotNull(response);
        // 2️⃣ ¡AQUÍ ESTÁ EL ARREGLO DEL ESTADO! Cambiamos a NO_CONTENT (204)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(detalleVentaService, times(1)).deleteDetalleVenta(id);
    }
}