package cl.duoc.kiosko.ventas.Controller;

import cl.duoc.kiosko.ventas.Assembler.VentaModelAssembler;
import cl.duoc.kiosko.ventas.Service.VentaService;
import cl.duoc.kiosko.ventas.dto.VentaRequestDTO;
import cl.duoc.kiosko.ventas.dto.VentaResponseDTO;
import cl.duoc.kiosko.ventas.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import cl.duoc.kiosko.ventas.Exception.GlobalExceptionHandler;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = {VentaController.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Nuestro "Postman" virtual

    @MockitoBean
    private VentaService ventaService; // Burlamos tu Servicio (no queremos tocar la BD)

    @MockitoBean
    private VentaModelAssembler assembler; // Burlamos HATEOAS

    @MockitoBean
    private JwtService jwtService;

    // Se instancia directo porque el slice @WebMvcTest de Spring Boot 4 no expone el bean ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper(); // Transforma objetos Java a JSON

    @Test
    @DisplayName("Debe buscar una venta por ID y devolver Status 200 OK")
    void testBuscarVentaId() throws Exception {
        // 1. GIVEN / ARRANGE (Preparar)
        VentaResponseDTO ventaMock = new VentaResponseDTO();
        ventaMock.setId(1L);
        ventaMock.setTotal(5000);
        ventaMock.setDetalles(new ArrayList<>()); // Evita nulos

        // Le decimos a los mocks qué devolver
        when(ventaService.findVentaDTO(1L)).thenReturn(ventaMock);
        when(assembler.toModel(any())).thenReturn(ventaMock);

        // 2 & 3. WHEN / ACT & THEN / ASSERT (Actuar y Afirmar)
        mockMvc.perform(get("/v1/ventas/1")) // Hacemos GET a la URL
                .andExpect(status().isOk()) // Esperamos un HTTP 200
                .andExpect(jsonPath("$.id").value(1)) // Validamos que el JSON devuelva ID 1 (el campo del DTO se llama "id", no "ventaId")
                .andExpect(jsonPath("$.total").value(5000)); // Validamos que el JSON devuelva Total 5000
    }

    @Test
    @DisplayName("Debe crear una venta y devolver Status 201 Created")
    void testAgregarVenta() throws Exception {
        // 1. GIVEN / ARRANGE
        // Creamos lo que el usuario enviaría (Request)
        // OJO: debe pasar las validaciones @Valid del controller (fecha no nula y al menos 1 detalle)
        VentaRequestDTO requestBody = new VentaRequestDTO();
        requestBody.setTotal(10000);
        requestBody.setFechaHoraVenta(new java.util.Date());
        requestBody.setDetalles(java.util.List.of(new cl.duoc.kiosko.ventas.dto.DetalleVentaRequestDTO(1L, 2, 10000)));

        // Creamos lo que el sistema debería devolver (Response)
        VentaResponseDTO responseMock = new VentaResponseDTO();
        responseMock.setId(2L);
        responseMock.setTotal(10000);

        when(ventaService.saveVenta(any(VentaRequestDTO.class))).thenReturn(responseMock);
        when(assembler.toModel(any())).thenReturn(responseMock);

        //Convertimos el RequestDTO a formato texto JSON para la petición
        String jsonRequest = objectMapper.writeValueAsString(requestBody);

        // 2 & 3. WHEN / ACT & THEN / ASSERT
        mockMvc.perform(post("/v1/ventas") // Hacemos POST a la URL
                        .contentType(MediaType.APPLICATION_JSON) // Decimos que enviamos un JSON
                        .content(jsonRequest)) // Metemos el JSON
                .andExpect(status().isCreated()) // Esperamos HTTP 201 CREATED
                .andExpect(jsonPath("$.id").value(2)) // el campo del DTO se llama "id"
                .andExpect(jsonPath("$.total").value(10000));
    }

    @Test
    @DisplayName("Debe eliminar una venta y devolver Status 204 No Content")
    void testEliminarVentaId() throws Exception {
        // 1. GIVEN / ARRANGE
        // Como tu deleteVenta es un void, Mockito asume por defecto que funciona bien
        // No necesitamos un 'when' aquí.

        // 2 & 3. WHEN / ACT & THEN / ASSERT
        mockMvc.perform(delete("/v1/ventas/1")) // Hacemos DELETE a la URL
                .andExpect(status().isNoContent()); // Esperamos HTTP 204 NO CONTENT (Tal cual lo tienes en el Controller)
    }
    @Test
    void testEliminarVentaId_NoExiste_Devuelve404NotFound() throws Exception {
        // Configuramos el Mock del servicio para que lance la excepción cuando el controlador intente borrar
        org.mockito.Mockito.doThrow(new java.util.NoSuchElementException("No existe"))
                .when(ventaService).deleteVenta(999L);

        // Ejecutamos la petición HTTP DELETE
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/v1/ventas/999"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
        // .isNotFound() verifica que responda un código 404 gracias a tu GlobalExceptionHandler
    }
}