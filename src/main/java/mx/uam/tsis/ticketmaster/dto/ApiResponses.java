package mx.uam.tsis.ticketmaster.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;
import lombok.Data;
import lombok.AllArgsConstructor;

public class ApiResponses {

    @Value
    public static class TicketTypeInfo {
        String id;
        String name;
        String description;
        Double price;
        Double promotionalPrice;
        Integer availableQuantity;
        Integer maxPerPerson;
        LocalDateTime saleStartDate;
        LocalDateTime saleEndDate;
        String venueZone;
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Double getPrice() { return price; }
        public Double getPromotionalPrice() { return promotionalPrice; }
        public Integer getAvailableQuantity() { return availableQuantity; }
        public Integer getMaxPerPerson() { return maxPerPerson; }
        public LocalDateTime getSaleStartDate() { return saleStartDate; }
        public LocalDateTime getSaleEndDate() { return saleEndDate; }
        public String getVenueZone() { return venueZone; }
    }

    public static class EventDetailResponse {
        private String id;
        private String name;
        private String description;
        private String venue;
        private String category;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<TicketTypeInfo> ticketTypes;

        public EventDetailResponse(String id, String name, String description,
                                 String venue, String category,
                                 LocalDateTime startDate, LocalDateTime endDate,
                                 List<TicketTypeInfo> ticketTypes) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.venue = venue;
            this.category = category;
            this.startDate = startDate;
            this.endDate = endDate;
            this.ticketTypes = ticketTypes;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getVenue() { return venue; }
        public String getCategory() { return category; }
        public LocalDateTime getStartDate() { return startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public List<TicketTypeInfo> getTicketTypes() { return ticketTypes; }
    }

    public static class EventSummaryResponse {
        private String id;
        private String name;
        private LocalDateTime startDate;
        private String venue;
        private List<TicketTypeInfo> ticketTypes;
        private Integer totalReservations;

        public EventSummaryResponse(String id, String name,
                                  LocalDateTime startDate, String venue,
                                  List<TicketTypeInfo> ticketTypes,
                                  Integer totalReservations) {
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.venue = venue;
            this.ticketTypes = ticketTypes;
            this.totalReservations = totalReservations;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public LocalDateTime getStartDate() { return startDate; }
        public String getVenue() { return venue; }
        public List<TicketTypeInfo> getTicketTypes() { return ticketTypes; }
        public Integer getTotalReservations() { return totalReservations; }
    }


    public static class TicketTypeDetailResponse {
        private String id;
        private String name;
        private String description;
        private Double price;
        private Integer availableQuantity;
        private Integer maxPerPerson;
        private String venueZone;
        private LocalDateTime saleStartDate;
        private LocalDateTime saleEndDate;

        public TicketTypeDetailResponse(String id, String name, String description,
                                      Double price, Integer availableQuantity,
                                      Integer maxPerPerson, String venueZone,
                                      LocalDateTime saleStartDate,
                                      LocalDateTime saleEndDate) {
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

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Double getPrice() { return price; }
        public Integer getAvailableQuantity() { return availableQuantity; }
        public Integer getMaxPerPerson() { return maxPerPerson; }
        public String getVenueZone() { return venueZone; }
        public LocalDateTime getSaleStartDate() { return saleStartDate; }
        public LocalDateTime getSaleEndDate() { return saleEndDate; }
    }

    public static class TicketReservationResponse {
        private String reservationId;
        private String ticketTypeId;
        private String ticketTypeName;
        private String venueZone;
        private Integer quantity;
        private Double pricePerTicket;
        private Double totalPrice;
        private Boolean discountApplied;
        private LocalDateTime expiresAt;
        private Boolean active;

        public TicketReservationResponse(String reservationId, String ticketTypeId,
                                       String ticketTypeName, String venueZone,
                                       Integer quantity, Double pricePerTicket,
                                       Double totalPrice, Boolean discountApplied,
                                       LocalDateTime expiresAt, Boolean active) {
            this.reservationId = reservationId;
            this.ticketTypeId = ticketTypeId;
            this.ticketTypeName = ticketTypeName;
            this.venueZone = venueZone;
            this.quantity = quantity;
            this.pricePerTicket = pricePerTicket;
            this.totalPrice = totalPrice;
            this.discountApplied = discountApplied;
            this.expiresAt = expiresAt;
            this.active = active;
        }

        public String getReservationId() { return reservationId; }
        public String getTicketTypeId() { return ticketTypeId; }
        public String getTicketTypeName() { return ticketTypeName; }
        public String getVenueZone() { return venueZone; }
        public Integer getQuantity() { return quantity; }
        public Double getPricePerTicket() { return pricePerTicket; }
        public Double getTotalPrice() { return totalPrice; }
        public Boolean getDiscountApplied() { return discountApplied; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public Boolean isActive() { return active; }
    }

    @Data
    @AllArgsConstructor
    public static class EventPromotionResponse {
        private String eventId;
        private String name;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String venue;
        private String category;
        private double originalPrice;
        private double promotionalPrice;
        private double discountPercentage;
        private LocalDateTime promotionEndDate;
    }

    public static class SeatAvailabilityResponse {
        private String id;
        private String row;
        private String number;
        private String zone;
        private Double price;

        public SeatAvailabilityResponse(String id, String row, String number, String zone, Double price) {
            this.id = id;
            this.row = row;
            this.number = number;
            this.zone = zone;
            this.price = price;
        }

        // Getters
        public String getId() { return id; }
        public String getRow() { return row; }
        public String getNumber() { return number; }
        public String getZone() { return zone; }
        public Double getPrice() { return price; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
