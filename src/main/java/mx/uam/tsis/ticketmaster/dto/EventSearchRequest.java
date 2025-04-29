package mx.uam.tsis.ticketmaster.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventSearchRequest {
    private String query;
    private String name;
    private String category;
    private String venue;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Double minPrice;
    private Double maxPrice;
    private Boolean hasAvailability;

    public EventSearchRequest(String name, String category, Double minPrice, Double maxPrice, Boolean hasAvailability) {
        this.name = name;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.hasAvailability = hasAvailability;
    }
}
