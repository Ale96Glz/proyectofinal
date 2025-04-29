package mx.uam.tsis.ticketmaster.datos;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.Seat;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByTicketTypeAndAvailable(TicketType ticketType, boolean available);
    List<Seat> findByZone(String zone);
}
