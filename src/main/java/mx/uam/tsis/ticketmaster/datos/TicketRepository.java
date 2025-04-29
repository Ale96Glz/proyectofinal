package mx.uam.tsis.ticketmaster.datos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import mx.uam.tsis.ticketmaster.negocio.modelo.Ticket;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id AND t.status = 'AVAILABLE'")
    Optional<Ticket> findAvailableTicketById(String id);
    
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    
    Optional<Ticket> findFirstByTicketTypeAndStatus(TicketType ticketType, Ticket.TicketStatus status);
}
