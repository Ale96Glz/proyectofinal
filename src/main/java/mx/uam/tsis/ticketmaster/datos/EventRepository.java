package mx.uam.tsis.ticketmaster.datos;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByNameContainingIgnoreCase(String name);
    List<Event> findByCategoryIgnoreCase(String category);
    List<Event> findByVenueContainingIgnoreCase(String venue);
    List<Event> findByStartDateAfter(LocalDateTime date);
    List<Event> findByCategory(String category);
    List<Event> findByActive(boolean active);
}
