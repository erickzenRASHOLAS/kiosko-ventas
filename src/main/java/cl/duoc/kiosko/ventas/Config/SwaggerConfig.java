package cl.duoc.kiosko.ventas.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean// llama componentes externos (funciona como libreria)
    public OpenAPI customerOpenAPI(){

        // Configuración del servidor de producción con HTTPS (railway)
        Server productionServer = new Server();
        productionServer.setUrl("https://kiosko-ventas-production.up.railway.app");
        productionServer.setDescription("Servidor de Producción en Railway");

        // Configuración para probar en local
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Servidor de Desarrollo Local");

        return new OpenAPI()
                .info(new Info()
                        .title("API 2026 Ventas kiosko")
                        .version("1.0")
                        .description("Documentación de la API para el microservicio de ventas del kiosko"))
                .servers(List.of(productionServer, localServer)); // Agrega ambos servidores
    }
}
