package mx.uam.tsis.ticketmaster.negocio;

import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.datos.EventRepository;
import mx.uam.tsis.ticketmaster.datos.SeatRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.*;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketReservationResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.SeatAvailabilityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class TicketReservationServiceTest {

    @Mock
    private TicketReservationRepository reservationRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private TicketReservationService reservationService;

    private Event mockEvent;
    private TicketType mockTicketType;
    private TicketReservation mockReservation;
    private Set<String> mockSeatIds;
    private Set<Seat> mockSeats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockEvent = new Event();
        mockEvent.setId("EVENT-001");
        mockEvent.setName("Concierto Test");
        mockEvent.setStartDate(LocalDateTime.now().plusDays(1));
        mockEvent.setEndDate(LocalDateTime.now().plusDays(1).plusHours(3));
        mockEvent.setActive(true);
        mockEvent.setMaxTicketsPerPurchase(10);

        mockTicketType = new TicketType();
        mockTicketType.setId("TICKET-001");
        mockTicketType.setName("VIP");
        mockTicketType.setPrice(1000.0);
        mockTicketType.setQuantity(100);
        mockTicketType.setAvailableQuantity(50);
        mockTicketType.setMaxPerPerson(5);
        mockTicketType.setEvent(mockEvent);

        mockReservation = new TicketReservation();
        mockReservation.setId("RES-001");
        mockReservation.setTicketType(mockTicketType);
        mockReservation.setQuantity(2);
        mockReservation.setTotalPrice(2000.0);
        mockReservation.setActive(true);
        mockReservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        mockSeatIds = new HashSet<>(Arrays.asList("SEAT-001", "SEAT-002"));

        mockSeats = new HashSet<>();
        Seat seat1 = new Seat();
        seat1.setId("SEAT-001");
        seat1.setAvailable(true);
        seat1.setTicketType(mockTicketType);
        mockSeats.add(seat1);

        Seat seat2 = new Seat();
        seat2.setId("SEAT-002");
        seat2.setAvailable(true);
        seat2.setTicketType(mockTicketType);
        mockSeats.add(seat2);
    }

    @Test
    void crearReservacion_Exitoso() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId("TICKET-001", "EVENT-001")).thenReturn(Optional.of(mockTicketType));
        when(seatRepository.findAllById(mockSeatIds)).thenReturn(new ArrayList<>(mockSeats));
        when(reservationRepository.save(any(TicketReservation.class))).thenReturn(mockReservation);

        TicketReservationResponse response = reservationService.createReservation(
            "EVENT-001", "TICKET-001", 2, mockSeatIds);

        assertNotNull(response);
        assertEquals("RES-001", response.getReservationId());
        assertEquals(2, response.getQuantity());
        assertEquals(2000.0, response.getTotalPrice());
        assertTrue(response.isActive());
        verify(reservationRepository).save(any(TicketReservation.class));
    }

    @Test
    void crearReservacion_ConDescuento() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId("TICKET-001", "EVENT-001")).thenReturn(Optional.of(mockTicketType));
        when(reservationRepository.save(any(TicketReservation.class))).thenReturn(mockReservation);

        TicketReservationResponse response = reservationService.createReservation(
            "EVENT-001", "TICKET-001", 6, null);

        assertNotNull(response);
        assertEquals(6, response.getQuantity());
        // Verificar que se aplicó el descuento del 10%
        assertEquals(5400.0, response.getTotalPrice()); // 6 * 1000 * 0.9
    }

    @Test
    void crearReservacion_ExcedeMaximoTickets() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId("TICKET-001", "EVENT-001")).thenReturn(Optional.of(mockTicketType));

        assertThrows(IllegalArgumentException.class, () -> 
            reservationService.createReservation("EVENT-001", "TICKET-001", 11, null)
        );
    }

    @Test
    void crearReservacion_EventoNoEncontrado() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            reservationService.createReservation("EVENT-001", "TICKET-001", 2, null)
        );
    }

    @Test
    void crearReservacion_TipoTicketNoEncontrado() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId("TICKET-001", "EVENT-001")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            reservationService.createReservation("EVENT-001", "TICKET-001", 2, null)
        );
    }

    @Test
    void obtenerAsientosDisponibles_Exitoso() {
        when(eventRepository.findById("EVENT-001")).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId("TICKET-001", "EVENT-001")).thenReturn(Optional.of(mockTicketType));
        when(seatRepository.findByTicketTypeAndAvailable(mockTicketType, true)).thenReturn(new ArrayList<>(mockSeats));

        List<SeatAvailabilityResponse> seats = reservationService.getAvailableSeats("EVENT-001", "TICKET-001");

        assertNotNull(seats);
        assertEquals(2, seats.size());
        verify(seatRepository).findByTicketTypeAndAvailable(mockTicketType, true);
    }

    @Test
    void cancelarReservacion_Exitoso() {
        when(reservationRepository.findById("RES-001")).thenReturn(Optional.of(mockReservation));
        reservationService.cancelReservation("RES-001");

        verify(reservationRepository).delete(mockReservation);
    }

    @Test
    void cancelarReservacion_NoEncontrada() {
        when(reservationRepository.findById("RES-001")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> 
            reservationService.cancelReservation("RES-001")
        );
    }

    @Test
    void limpiarReservacionesExpiradas_DeberiaCancelarExpiradas() {
        LocalDateTime now = LocalDateTime.now();
        
        // Crear Event y TicketType
        Event event = new Event();
        event.setId("EVENT-001");
        event.setName("Test Event");
        event.setActive(true);
        
        TicketType ticketType = new TicketType();
        ticketType.setId("TICKET-001");
        ticketType.setName("VIP");
        ticketType.setEvent(event);
        ticketType.setAvailableQuantity(10);
        
        // Crear reservación expirada
        TicketReservation mockReservation = new TicketReservation();
        mockReservation.setId("RES-001");
        mockReservation.setTicketType(ticketType);
        mockReservation.setActive(true);
        mockReservation.setExpiresAt(now.minusMinutes(10));
        mockReservation.setQuantity(2);
        mockReservation.setTotalPrice(2000.0);

        when(reservationRepository.findByActiveAndExpiresAtBefore(eq(true), any(LocalDateTime.class)))
            .thenReturn(Collections.singletonList(mockReservation));
        when(reservationRepository.findById(mockReservation.getId()))
            .thenReturn(Optional.of(mockReservation));

        reservationService.cleanupExpiredReservations();

        verify(reservationRepository).findByActiveAndExpiresAtBefore(eq(true), any(LocalDateTime.class));
        verify(reservationRepository).delete(mockReservation);
    }

    @Test
    void limpiarReservacionesExpiradas_NoDeberiaCancelarActivas() {
        when(reservationRepository.findByActiveAndExpiresAtBefore(eq(true), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        reservationService.cleanupExpiredReservations();

        verify(reservationRepository).findByActiveAndExpiresAtBefore(eq(true), any(LocalDateTime.class));
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void crearReservacion_DeberiaCrearExitosamente() {
        String eventId = "EVENT-001";
        String ticketTypeId = "TICKET-001";
        int quantity = 2;
        
        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("Test Event");
        mockEvent.setStartDate(LocalDateTime.now().plusDays(7));
        mockEvent.setEndDate(LocalDateTime.now().plusDays(7).plusHours(3));
        mockEvent.setActive(true);
        mockEvent.setMaxTicketsPerPurchase(10);
        
        TicketType mockTicketType = new TicketType();
        mockTicketType.setId(ticketTypeId);
        mockTicketType.setEvent(mockEvent);
        mockTicketType.setAvailableQuantity(10);
        mockTicketType.setPrice(1000.0);
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        when(ticketTypeRepository.findByIdAndEventId(ticketTypeId, eventId)).thenReturn(Optional.of(mockTicketType));
        when(reservationRepository.save(any(TicketReservation.class)))
            .thenAnswer(invocation -> {
                TicketReservation savedReservation = invocation.getArgument(0);
                savedReservation.setId("RES-001");
                return savedReservation;
            });
        when(ticketTypeRepository.save(any(TicketType.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
            
        TicketReservationResponse result = reservationService.createReservation(eventId, ticketTypeId, quantity, null);
        
        assertNotNull(result);
        assertEquals("RES-001", result.getReservationId());
        assertEquals(ticketTypeId, result.getTicketTypeId());
        assertEquals(quantity, result.getQuantity());
        assertThat(result.isActive()).isTrue();
        assertEquals(mockTicketType.getPrice() * quantity, result.getTotalPrice());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(ticketTypeRepository).save(any(TicketType.class));
    }
} 