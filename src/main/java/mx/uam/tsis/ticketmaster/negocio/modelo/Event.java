package mx.uam.tsis.ticketmaster.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "events")
public class Event {
    
    public enum EventStatus {
        DRAFT,
        PUBLISHED,
        CANCELLED,
        COMPLETED
    }

    @Id
    private String id;
    
    private String name;
    private String description;
    private String venue;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxTicketsPerPurchase = 10; // Por defecto, máximo 10 tickets por compra
    
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<TicketType> ticketTypes = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el evento está activo para reservaciones
     * @return true si el evento está publicado y no ha sido cancelado o completado
     */
    public boolean isActive() {
        if (!active) {
            return false;
        }
        
        if (status != EventStatus.PUBLISHED) {
            return false;
        }
        
        if (startDate == null) {
            return false;
        }
        
        return !isPast();
    }

    /**
     * Establece si el evento está activo
     * @param active true para activar el evento, false para desactivarlo
     */
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            this.status = EventStatus.PUBLISHED;
        } else {
            this.status = EventStatus.CANCELLED;
        }
    }

    /**
     * Obtiene la fecha del evento
     * @return la fecha de inicio del evento
     */
    public LocalDateTime getEventDate() {
        return startDate;
    }

    /**
     * Establece la fecha del evento
     * @param eventDate la nueva fecha del evento
     */
    public void setEventDate(LocalDateTime eventDate) {
        this.startDate = eventDate;
    }

    public boolean isPast() {
        return startDate != null && startDate.isBefore(LocalDateTime.now());
    }

    public boolean isCancelled() {
        return status == EventStatus.CANCELLED;
    }

    public int getDurationInHours() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (int) java.time.Duration.between(startDate, endDate).toHours();
    }
}
