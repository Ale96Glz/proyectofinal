package mx.uam.tsis.ticketmaster.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ticket_types")
public class TicketType {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    private Double price;
    private Double promotionalPrice;
    private Integer quantity;
    private Integer availableQuantity;
    private Integer maxPerPerson;
    private Integer soldQuantity;
    
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;
    private String venueZone;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Double getPromotionalPrice() { return promotionalPrice; }
    public void setPromotionalPrice(Double promotionalPrice) { this.promotionalPrice = promotionalPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public Integer getMaxPerPerson() { return maxPerPerson; }
    public void setMaxPerPerson(Integer maxPerPerson) { this.maxPerPerson = maxPerPerson; }

    public Integer getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(Integer soldQuantity) { this.soldQuantity = soldQuantity; }

    public LocalDateTime getSaleStartDate() { return saleStartDate; }
    public void setSaleStartDate(LocalDateTime saleStartDate) { this.saleStartDate = saleStartDate; }

    public LocalDateTime getSaleEndDate() { return saleEndDate; }
    public void setSaleEndDate(LocalDateTime saleEndDate) { this.saleEndDate = saleEndDate; }

    public String getVenueZone() { return venueZone; }
    public void setVenueZone(String venueZone) { this.venueZone = venueZone; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    /**
     * Verifica si hay tickets disponibles
     * @return true si hay tickets disponibles
     */
    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    /**
     * Verifica si hay suficiente cantidad de tickets disponibles
     * @param quantity cantidad de tickets requeridos
     * @return true si hay suficiente cantidad disponible
     */
    public boolean hasAvailableQuantity(int quantity) {
        return availableQuantity >= quantity;
    }

    /**
     * Reserva una cantidad de tickets
     * @param quantity cantidad de tickets a reservar
     */
    public void reserveTickets(int quantity) {
        if (!hasAvailableQuantity(quantity)) {
            throw new IllegalStateException("No hay suficientes tickets disponibles");
        }
        availableQuantity -= quantity;
    }

    /**
     * Libera una cantidad de tickets
     * @param quantity cantidad de tickets a liberar
     */
    public void releaseTickets(int quantity) {
        availableQuantity += quantity;
    }

    /**
     * Calcula el precio total para una cantidad de tickets
     * @param quantity cantidad de tickets
     * @return precio total con descuento si aplica
     */
    public double calculateTotalPrice(int quantity) {
        double totalPrice = price * quantity;
        if (quantity > 5) {
            totalPrice = totalPrice * 0.9; // 10% de descuento
        }
        return totalPrice;
    }

    public boolean isWithinSalePeriod() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(saleStartDate) && now.isBefore(saleEndDate);
    }
}
