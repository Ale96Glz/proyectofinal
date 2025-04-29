package mx.uam.tsis.ticketmaster.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {
    
    public enum TicketStatus {
        AVAILABLE,
        RESERVED,
        SOLD,
        USED,
        CANCELLED
    }

    @Id
    private String id;

    @Column(unique = true)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @ManyToOne
    private User user;

    private LocalDateTime purchaseDate;
    private LocalDateTime validUntil;
    private String seatInformation;
    private String qrCode;
}
