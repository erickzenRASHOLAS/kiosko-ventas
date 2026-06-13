package cl.duoc.kiosko.ventas.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        //Retorna la instancia configurada para que Spring la inyecte donde la necesitemos
        //Se usa el builder que entrega Spring Boot (trae la configuración por defecto del framework)
        //TODO: cuando se integre el microservicio de productos, fijar aquí su baseUrl
        return builder.build();
    }

}
