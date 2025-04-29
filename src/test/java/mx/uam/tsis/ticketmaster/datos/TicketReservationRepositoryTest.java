package mx.uam.tsis.ticketmaster.datos;

import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketReservationRepository reservationRepository;

    @Test
    void buscarReservacionesActivas_DeberiaRetornarActivas() {
        LocalDateTime now = LocalDateTime.now();
        Event event = new Event();
        event.setId("EVENT-001");
        event.setName("Test Event");
        event.setActive(true);
        entityManager.persist(event);

        TicketType ticketType = new TicketType();
        ticketType.setId("TICKET-001");
        ticketType.setName("VIP");
        ticketType.setEvent(event);
        entityManager.persist(ticketType);

        // Crear reservación activa
        TicketReservation activeReservation = new TicketReservation();
        activeReservation.setId("RES-001");
        activeReservation.setTicketType(ticketType);
        activeReservation.setQuantity(2);
        activeReservation.setActive(true);
        activeReservation.setExpiresAt(now.plusMinutes(5));
        activeReservation.setTotalPrice(2000.0);
        entityManager.persist(activeReservation);

        entityManager.flush();

        List<TicketReservation> activeReservations = 
            reservationRepository.findByActiveAndExpiresAtAfter(true, now);

        assertThat(activeReservations).hasSize(1);
        assertThat(activeReservations.get(0).getId()).isEqualTo("RES-001");
    }

    @Test
    void buscarReservacionPorId_DeberiaRetornarReservacion() {
        Event event = new Event();
        event.setId("EVENT-001");
        event.setName("Test Event");
        event.setActive(true);
        entityManager.persist(event);

        TicketType ticketType = new TicketType();
        ticketType.setId("TICKET-001");
        ticketType.setName("VIP");
        ticketType.setEvent(event);
        entityManager.persist(ticketType);

        TicketReservation reservation = new TicketReservation();
        reservation.setId("RES-001");
        reservation.setTicketType(ticketType);
        reservation.setQuantity(2);
        reservation.setActive(true);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        reservation.setTotalPrice(2000.0);
        entityManager.persist(reservation);

        entityManager.flush();

        Optional<TicketReservation> found = reservationRepository.findById("RES-001");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("RES-001");
        assertThat(found.get().getQuantity()).isEqualTo(2);
        assertThat(found.get().getActive()).isTrue();
    }
}