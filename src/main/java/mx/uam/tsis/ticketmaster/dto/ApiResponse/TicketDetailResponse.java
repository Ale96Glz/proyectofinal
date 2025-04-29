package mx.uam.tsis.ticketmaster.dto.ApiResponse;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDetailResponse {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer availableQuantity;
    private Integer maxPerPerson;
    private String venueZone;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;

    public TicketDetailResponse(String id, String name, String description, Double price, Integer availableQuantity, Integer maxPerPerson, String venueZone, LocalDateTime saleStartDate, LocalDateTime saleEndDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.maxPerPerson = maxPerPerson;
        this.venueZone = venueZone;
        this.saleStartDate = saleStartDate;
        this.saleEndDate = saleEndDate;
    }
}
