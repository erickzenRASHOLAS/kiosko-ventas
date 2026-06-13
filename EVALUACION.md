# Evaluación: microservicio `ventas`

> Fecha: 2026-06-12 — Evaluación realizada con Claude Code.
> Estado de tests al cierre: **6/6 pasando** (3 `VentaServiceTest` + 3 `VentaControllerTest`).

---

## 🔴 Lo que estaba roto (y ya se arregló)

El problema "no puedo implementar Mockito con JUnit 5" **no era Mockito** — era **Spring Boot 4**, que cambió cosas en los tests:

| Problema | Causa | Fix aplicado |
|---|---|---|
| No compilaba `VentaControllerTest` | `@MockBean` fue **eliminado** en Boot 4 y `@WebMvcTest` cambió de paquete | Import nuevo: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` y se eliminó el import muerto de `MockBean` |
| `ObjectMapper` no inyectaba | El slice `@WebMvcTest` de Boot 4 ya no expone ese bean | Se instancia directo: `new ObjectMapper()` |
| `No value at JSON path "$.ventaId"` | El DTO serializa el campo como `id`, no `ventaId` | jsonPath corregido a `$.id` |
| `testAgregarVenta` daba 500 | El request solo tenía `total`; `@Valid` rechazaba `fechaHoraVenta` y `detalles` nulos | El test ahora manda un request válido |

`VentaServiceTest` estaba **correcto desde el principio** — Mockito + JUnit 5 puro siempre funcionó; lo roto era el test de controller.

## 🟡 Bugs reales encontrados en el código de producción

1. **Errores de validación devuelven 500 en vez de 400** — `src/main/java/cl/duoc/kiosko/ventas/Exception/GlobalExceptionHandler.java:40` (el catch-all) atrapa `MethodArgumentNotValidException` y responde `INTERNAL_SERVER_ERROR`. Confirmado en vivo con el test. Falta un handler específico que devuelva `400 BAD_REQUEST` con los mensajes de cada campo.

2. **`log.error` para todo** — `VentaService.java` usa `log.error("Se Listan todas los Ventas")` para operaciones normales. Eso ensucia el log de errores. `DetalleVentaService` lo hace bien con `log.info` — copiar ese estilo.

3. **El cliente manda el `total` y `subtotal`** — el servidor confía ciegamente en lo que llega. Cualquiera puede crear una venta de total $1. El total debería calcularse en el servidor (suma de subtotales, y el precio debería venir del microservicio de productos).

4. **`WebClientConfig.java:11` ignora el builder** — recibe `WebClient.Builder builder` pero retorna `WebClient.create()`. Además el `WebClient` se inyecta en `DetalleVentaService` pero **nunca se usa** (la integración con productos está pendiente — `ProductoDTO` también está huérfano).

5. **`@NotNull` sobre `int` no hace nada** — en `Venta.total`, `DetalleVenta.cantidad/subtotal` y los DTOs: un primitivo `int` jamás puede ser null (llega como 0). Usar `Integer` o solo `@Min`.

6. **`updateVenta` no toca los detalles** — un PUT cambia fecha/total pero el total puede quedar inconsistente con los subtotales de los detalles.

## 🟠 Pendientes evidentes

- **Seguridad: solo cascarones** — `SecurityConfig`, `JwtAuthenticationFilter` y `JwtSecurity` están vacíos. Las dependencias `jjwt` están en el pom pero **falta `spring-boot-starter-security`**. `UserRole` tampoco se usa aún.
- **Scopes en el pom** — `junit-jupiter-api`, `mockito-junit-jupiter` y `spring-test` están **sin `<scope>test</scope>`**, así que se filtran al classpath de producción. Además `spring-boot-starter-test` ya trae JUnit 5 + Mockito; varias de esas dependencias explícitas sobran.
- **Config de producción peligrosa** — `application.properties` con root sin password, `TRACE`/`show-sql` activados, y DataFaker corriendo en cualquier arranque. Para el ramo está bien, pero lo correcto es separar en perfiles (`application-dev.properties`) y anotar `DataFakerConfig` con `@Profile("dev")`.
- **`VentasApplicationTests` (contextLoads) requiere MySQL corriendo** — fallará en cualquier máquina sin la BD levantada.

## 🟢 Lo que está bien hecho

- **Arquitectura en capas limpia**: Controller → Service → Repository con DTOs separados de Request/Response.
- **HATEOAS con assemblers** bien centralizado (`VentaModelAssembler`), incluyendo el `removeLinks()` para evitar duplicados.
- **Flyway + `ddl-auto=validate`** — excelente práctica, la mayoría deja `update`.
- **Swagger documentado** con `@Operation` en cada endpoint y bean `OpenAPI` propio.
- La relación bidireccional `Venta`↔`DetalleVenta` con cascada y `orphanRemoval` está correcta, igual que el `@JsonIgnore`/`@ToString.Exclude` para cortar los ciclos.
- `VentaService` con inyección por constructor (`@RequiredArgsConstructor`) — extender eso a los demás (los controllers y `DetalleVentaService` usan `@Autowired` en campos).

---

## ✅ Correcciones aplicadas (2026-06-12)

Tras la evaluación se aplicaron estas correcciones (tests 6/6 pasando):

1. **`GlobalExceptionHandler`**: nuevo handler de `MethodArgumentNotValidException` → responde `400` con el detalle campo por campo (antes el catch-all respondía `500`). `MethodArgumentTypeMismatchException` también corregido de `500` → `400`.
2. **`ExceptionDTO`**: constructor sobrecargado `(HttpStatus, String)` para mensajes armados a mano.
3. **`VentaService`**: `log.error` → `log.info`; el `total` ahora lo **calcula el servidor** (suma de subtotales) en `saveVenta` y `updateVenta`; `updateVenta` además reemplaza los detalles del request (con `orphanRemoval`) en vez de ignorarlos.
4. **`DetalleVentaService`**: `@Transactional` + inyección por constructor; al crear/editar/borrar un detalle se **recalcula el total de la venta padre** (antes quedaba desactualizado).
5. **`WebClientConfig`**: usa el `WebClient.Builder` de Spring (antes lo recibía y lo ignoraba).
6. **`Integer` en vez de `int`** donde había `@NotNull` (entidades y request DTOs) — sobre un primitivo esa validación no hacía nada. `VentaRequestDTO.total` quedó opcional/ignorado porque ahora lo calcula el servidor.
7. **Inyección por constructor** en ambos controllers (`@RequiredArgsConstructor`); se eliminó el `VentaService` inyectado y no usado en `DetalleVentaController`.
8. **`pom.xml`**: `<scope>test</scope>` agregado a `junit-jupiter-api`, `mockito-junit-jupiter` y `spring-test` (se filtraban al classpath de producción).
9. **`DataFakerConfig`**: `@Profile("!prod")` — sigue sembrando datos en desarrollo, pero no en producción.

## Pendientes (no abordados, son features)

1. Integrar precios desde el microservicio de **productos** vía `WebClient` (el subtotal aún lo manda el cliente; `ProductoDTO` sigue sin usarse).
2. Implementar **JWT + Spring Security** (agregar `spring-boot-starter-security` y completar los cascarones de `security/`).
3. Separar configuración en perfiles (`application-dev.properties` / `application-prod.properties`).
4. `VentasApplicationTests` (contextLoads) sigue requiriendo MySQL corriendo.
