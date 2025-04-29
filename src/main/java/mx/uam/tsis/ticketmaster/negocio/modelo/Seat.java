package mx.uam.tsis.ticketmaster.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private TicketType ticketType;

    @Column(name = "seat_row")
    private String seatRow;

    private String number;
    private String zone;
    private boolean available;

    // Constructor por defecto
    public Seat() {}

    // Constructor con parámetros
    public Seat(TicketType ticketType, String seatRow, String number, String zone) {
        this.ticketType = ticketType;
        this.seatRow = seatRow;
        this.number = number;
        this.zone = zone;
        this.available = true;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }

    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
