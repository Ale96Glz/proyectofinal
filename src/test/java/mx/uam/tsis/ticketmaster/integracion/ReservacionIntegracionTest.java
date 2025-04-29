package mx.uam.tsis.ticketmaster.integracion;

import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.User;
import mx.uam.tsis.ticketmaster.datos.EventRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.datos.UserRepository;
import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservacionIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketReservationRepository reservationRepository;

    private Event evento;
    private TicketType tipoTicket;
    private User usuario;

    @BeforeEach
    void setUp() {
        // Crear usuario
        usuario = new User();
        usuario.setId("USER-001");
        usuario.setFirstName("Test");
        usuario.setLastName("User");
        usuario.setEmail("test@example.com");
        usuario.setRole(User.UserRole.ATTENDEE);
        userRepository.save(usuario);

        // Crear evento
        evento = new Event();
        evento.setId("EVENT-001");
        evento.setName("Concierto Test");
        evento.setDescription("Concierto de prueba");
        evento.setVenue("Estadio Test");
        evento.setStartDate(LocalDateTime.now().plusDays(7));
        evento.setEndDate(LocalDateTime.now().plusDays(7).plusHours(3));
        evento.setActive(true);
        evento.setMaxTicketsPerPurchase(10);
        evento.setCreatedBy(usuario);
        eventRepository.save(evento);

        // Crear tipo de ticket
        tipoTicket = new TicketType();
        tipoTicket.setId("TICKET-001");
        tipoTicket.setName("VIP");
        tipoTicket.setDescription("Asiento VIP");
        tipoTicket.setPrice(1000.0);
        tipoTicket.setQuantity(100);
        tipoTicket.setAvailableQuantity(50);
        tipoTicket.setMaxPerPerson(5);
        tipoTicket.setEvent(evento);
        ticketTypeRepository.save(tipoTicket);
    }

    @Test
    void reservarTickets_FlujoCompleto() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").exists())
            .andExpect(jsonPath("$.ticketTypeId").value("TICKET-001"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.totalPrice").value(2000.0))
            .andExpect(jsonPath("$.active").value(true));

        // Verificar que se actualizó la cantidad disponible
        TicketType ticketTypeActualizado = ticketTypeRepository.findById("TICKET-001").orElseThrow();
        assertEquals(48, ticketTypeActualizado.getAvailableQuantity());
    }

    @Test
    void reservarTickets_ConDescuento() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").exists())
            .andExpect(jsonPath("$.quantity").value(6))
            .andExpect(jsonPath("$.totalPrice").value(5400.0)) // 6 * 1000 * 0.9
            .andExpect(jsonPath("$.discountApplied").value(true));

        // Verificar que se actualizó la cantidad disponible
        TicketType ticketTypeActualizado = ticketTypeRepository.findById("TICKET-001").orElseThrow();
        assertEquals(44, ticketTypeActualizado.getAvailableQuantity());
    }

    @Test
    void reservarTickets_ExcedeMaximo() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "11")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // Verificar que no se modificó la cantidad disponible
        TicketType ticketTypeActualizado = ticketTypeRepository.findById("TICKET-001").orElseThrow();
        assertEquals(50, ticketTypeActualizado.getAvailableQuantity());
    }

    @Test
    void reservarTickets_SinDisponibilidad() throws Exception {
        // Actualizar cantidad disponible a 0
        tipoTicket.setAvailableQuantity(0);
        ticketTypeRepository.save(tipoTicket);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("No hay suficientes entradas disponibles"));
    }
} 