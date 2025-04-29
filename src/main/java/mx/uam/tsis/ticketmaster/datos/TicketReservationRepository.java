package mx.uam.tsis.ticketmaster.datos;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketReservationRepository extends JpaRepository<TicketReservation, String> {
    List<TicketReservation> findByActiveAndExpiresAtBefore(boolean active, LocalDateTime expiresAt);
    List<TicketReservation> findByActiveAndExpiresAtAfter(boolean active, LocalDateTime expiresAt);
    List<TicketReservation> findByActive(boolean active);
    List<TicketReservation> findByTicketType_Id(String ticketTypeId);
}
