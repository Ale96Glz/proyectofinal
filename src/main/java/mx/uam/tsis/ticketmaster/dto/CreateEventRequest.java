package mx.uam.tsis.ticketmaster.dto;

import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para crear un nuevo evento")
public class CreateEventRequest {
    @Schema(description = "Nombre del evento", example = "Concierto de Rock")
    private String name;

    @Schema(description = "Descripción del evento", example = "Gran concierto de rock con bandas internacionales")
    private String description;

    @Schema(description = "Categoría del evento", example = "Concierto")
    private String category;

    @Schema(description = "Lugar del evento", example = "Foro Sol")
    private String venue;

    @Schema(description = "Fecha y hora de inicio del evento")
    private LocalDateTime startDate;

    @Schema(description = "Fecha y hora de fin del evento")
    private LocalDateTime endDate;

    @Schema(description = "Tipos de tickets disponibles para el evento")
    private List<CreateTicketTypeRequest> ticketTypes;

    // Constructor
    public CreateEventRequest() {}

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public List<CreateTicketTypeRequest> getTicketTypes() { return ticketTypes; }
    public void setTicketTypes(List<CreateTicketTypeRequest> ticketTypes) { this.ticketTypes = ticketTypes; }

    public static class CreateTicketTypeRequest {
        @Schema(description = "Nombre del tipo de ticket", example = "VIP")
        private String name;

        @Schema(description = "Zona del venue", example = "Zona VIP")
        private String venueZone;

        @Schema(description = "Precio por ticket", example = "1500.0")
        private Double price;

        @Schema(description = "Cantidad total de tickets disponibles", example = "100")
        private Integer quantity;

        @Schema(description = "Cantidad máxima de tickets por persona", example = "4")
        private Integer maxPerPerson;

        @Schema(description = "Fecha de inicio de venta")
        private LocalDateTime saleStartDate;

        @Schema(description = "Fecha de fin de venta")
        private LocalDateTime saleEndDate;

        // Constructor
        public CreateTicketTypeRequest() {}

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVenueZone() { return venueZone; }
        public void setVenueZone(String venueZone) { this.venueZone = venueZone; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Integer getMaxPerPerson() { return maxPerPerson; }
        public void setMaxPerPerson(Integer maxPerPerson) { this.maxPerPerson = maxPerPerson; }

        public LocalDateTime getSaleStartDate() { return saleStartDate; }
        public void setSaleStartDate(LocalDateTime saleStartDate) { this.saleStartDate = saleStartDate; }

        public LocalDateTime getSaleEndDate() { return saleEndDate; }
        public void setSaleEndDate(LocalDateTime saleEndDate) { this.saleEndDate = saleEndDate; }
    }
}
