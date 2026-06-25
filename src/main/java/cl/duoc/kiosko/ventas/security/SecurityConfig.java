package cl.duoc.kiosko.ventas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación (si tienes)
                        .requestMatchers("/v1/auth/**").permitAll()

                        // RUTAS DE SWAGGER LIBERADAS POR COMPLETO
                        .requestMatchers(
                                "/doc/**",               // El que ya tenías
                                "/v3/api-docs/**",       // Los datos de la API
                                "/swagger-ui/**",        // La interfaz gráfica
                                "/swagger-ui.html",      // El HTML base
                                "/swagger-resources/**", // Recursos de Swagger
                                "/webjars/**"            // Archivos CSS y JS de Swagger
                        ).permitAll()

                        // El resto de la API de ventas requiere token obligatorio
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}