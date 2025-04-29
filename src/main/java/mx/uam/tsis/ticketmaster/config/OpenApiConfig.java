package mx.uam.tsis.ticketmaster.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("Local Development Server");

        return new OpenAPI()
                .servers(List.of(localServer))
                .info(new Info()
                        .title("API de Ticketmaster")
                        .description("Sistema de reserva de entradas para eventos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ticketmaster")
                                .email("aosorio961020@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
