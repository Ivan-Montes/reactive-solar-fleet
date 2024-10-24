package dev.ime.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/**").permitAll()
                .pathMatchers("/v3/api-docs/**","/swagger-ui.html","/webjars/swagger-ui/**").permitAll()
                .pathMatchers("/actuator/metrics").authenticated()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .csrf(CsrfSpec::disable)			
            .oauth2ResourceServer( resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
            .build();
        
    }
    
}
