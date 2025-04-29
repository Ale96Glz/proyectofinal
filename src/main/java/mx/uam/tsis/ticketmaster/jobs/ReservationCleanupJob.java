package mx.uam.tsis.ticketmaster.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationCleanupJob {

    @Autowired
    private TicketReservationRepository ticketReservationRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    /**
     * Job que se ejecuta cada minuto para limpiar las reservas expiradas
     */
    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    @Transactional
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Buscar todas las reservas activas que han expirado
        List<TicketReservation> expiredReservations = 
            ticketReservationRepository.findByActiveAndExpiresAtBefore(true, now);

        for (TicketReservation reservation : expiredReservations) {
            // Obtener el tipo de ticket
            TicketType ticketType = reservation.getTicketType();
            
            // Restaurar la cantidad disponible
            ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() + reservation.getQuantity()
            );
            
            // Marcar la reserva como inactiva
            reservation.setActive(false);
            
            // Guardar los cambios
            ticketTypeRepository.save(ticketType);
            ticketReservationRepository.save(reservation);
        }
    }
}
