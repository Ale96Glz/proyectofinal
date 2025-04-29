package mx.uam.tsis.ticketmaster.api;

import mx.uam.tsis.ticketmaster.negocio.TicketReservationService;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketReservationResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.SeatAvailabilityResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketReservationService reservationService;

    @Test
    void crearReservacion_Exitoso() throws Exception {
        // Arrange
        TicketReservationResponse mockResponse = new TicketReservationResponse(
            "RES-001", "TICKET-001", "VIP", "Zona A", 2, 1000.0, 2000.0, false, 
            LocalDateTime.now().plusMinutes(5), true
        );
        when(reservationService.createReservation(anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value("RES-001"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.totalPrice").value(2000.0))
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void crearReservacion_ConDescuento() throws Exception {
        // Arrange
        TicketReservationResponse mockResponse = new TicketReservationResponse(
            "RES-001", "TICKET-001", "VIP", "Zona A", 6, 1000.0, 5400.0, true,
            LocalDateTime.now().plusMinutes(5), true
        );
        when(reservationService.createReservation(anyString(), anyString(), anyInt(), any()))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(6))
            .andExpect(jsonPath("$.totalPrice").value(5400.0))
            .andExpect(jsonPath("$.discountApplied").value(true));
    }

    @Test
    void crearReservacion_EventoNoEncontrado() throws Exception {
        // Arrange
        when(reservationService.createReservation(anyString(), anyString(), anyInt(), any()))
            .thenThrow(new IllegalArgumentException("Evento no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void crearReservacion_SinDisponibilidad() throws Exception {
        // Arrange
        when(reservationService.createReservation(anyString(), anyString(), anyInt(), any()))
            .thenThrow(new IllegalStateException("No hay suficientes tickets disponibles"));

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .param("eventId", "EVENT-001")
                .param("ticketTypeId", "TICKET-001")
                .param("quantity", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("No hay suficientes tickets disponibles"));
    }

    @Test
    void obtenerAsientosDisponibles_Exitoso() throws Exception {
        // Arrange
        List<SeatAvailabilityResponse> mockSeats = Arrays.asList(
            new SeatAvailabilityResponse("SEAT-001", "A", "1", "VIP", 1000.0),
            new SeatAvailabilityResponse("SEAT-002", "A", "2", "VIP", 1000.0)
        );
        when(reservationService.getAvailableSeats(anyString(), anyString()))
            .thenReturn(mockSeats);

        // Act & Assert
        mockMvc.perform(get("/api/reservations/events/EVENT-001/seats/TICKET-001")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("SEAT-001"))
            .andExpect(jsonPath("$[0].number").value("1"))
            .andExpect(jsonPath("$[0].zone").value("VIP"));
    }

    @Test
    void cancelarReservacion_Exitoso() throws Exception {
        // Arrange
        doNothing().when(reservationService).cancelReservation(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/reservations/RES-001")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Reservación cancelada exitosamente"))
            .andExpect(jsonPath("$.reservationId").value("RES-001"));
    }

    @Test
    void cancelarReservacion_NoEncontrada() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Reservación no encontrada"))
            .when(reservationService).cancelReservation(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/reservations/RES-001")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
} 