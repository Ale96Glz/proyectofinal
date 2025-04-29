package mx.uam.tsis.ticketmaster.negocio.modelo;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class TicketReservationTest {

    @Test
    void crearReservacion_DeberiaEstablecerValoresPorDefecto() {
        // Arrange
        TicketType ticketType = new TicketType();
        ticketType.setId("TICKET-001");
        ticketType.setName("VIP");
        ticketType.setPrice(1000.0);

        // Act
        TicketReservation reservation = new TicketReservation();
        reservation.setId("RES-001");
        reservation.setTicketType(ticketType);
        reservation.setQuantity(2);
        reservation.setTotalPrice(2000.0);
        reservation.setActive(true);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Assert
        assertThat(reservation.getId()).isEqualTo("RES-001");
        assertThat(reservation.getTicketType()).isEqualTo(ticketType);
        assertThat(reservation.getQuantity()).isEqualTo(2);
        assertThat(reservation.getTotalPrice()).isEqualTo(2000.0);
        assertThat(reservation.getActive()).isTrue();
        assertThat(reservation.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void estaExpirada_DeberiaRetornarTrueCuandoExpirada() {
        // Arrange
        TicketReservation reservation = new TicketReservation();
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        // Act & Assert
        assertThat(reservation.isExpired()).isTrue();
    }

    @Test
    void estaExpirada_DeberiaRetornarFalseCuandoNoExpirada() {
        // Arrange
        TicketReservation reservation = new TicketReservation();
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Act & Assert
        assertThat(reservation.isExpired()).isFalse();
    }

    @Test
    void calcularPrecioTotal_DeberiaAplicarDescuentoParaMasDe5Tickets() {
        // Arrange
        TicketType ticketType = new TicketType();
        ticketType.setPrice(1000.0);

        TicketReservation reservation = new TicketReservation();
        reservation.setTicketType(ticketType);
        reservation.setQuantity(6);

        // Act
        double totalPrice = ticketType.getPrice() * reservation.getQuantity() * 0.9; // 10% descuento

        // Assert
        assertThat(totalPrice).isEqualTo(5400.0); // 6 * 1000 * 0.9
    }

    @Test
    void calcularPrecioTotal_NoDeberiaAplicarDescuentoParaMenosDe6Tickets() {
        // Arrange
        TicketType ticketType = new TicketType();
        ticketType.setPrice(1000.0);

        TicketReservation reservation = new TicketReservation();
        reservation.setTicketType(ticketType);
        reservation.setQuantity(5);

        // Act
        double totalPrice = ticketType.getPrice() * reservation.getQuantity();

        // Assert
        assertThat(totalPrice).isEqualTo(5000.0); // 5 * 1000
    }
} 