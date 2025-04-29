package mx.uam.tsis.ticketmaster.datos;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import java.util.List;
import java.util.Optional;

public interface TicketTypeRepository extends JpaRepository<TicketType, String> {
    List<TicketType> findByEvent(Event event);
    Optional<TicketType> findByIdAndEvent(String id, Event event);
    Optional<TicketType> findByIdAndEventId(String id, String eventId);
}
