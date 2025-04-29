package mx.uam.tsis.ticketmaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import mx.uam.tsis.ticketmaster.negocio.TicketReservationService;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    @Autowired
    private TicketReservationService reservationService;
    
    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    public void cleanupExpiredReservations() {
        reservationService.cleanupExpiredReservations();
    }
}
