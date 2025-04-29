package mx.uam.tsis.ticketmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "mx.uam.tsis.ticketmaster.negocio.modelo")
@EnableJpaRepositories(basePackages = "mx.uam.tsis.ticketmaster.datos")
@EnableScheduling
public class TicketmasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketmasterApplication.class, args);
    }
}
