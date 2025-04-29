package mx.uam.tsis.ticketmaster.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.datos.EventRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.*;
import mx.uam.tsis.ticketmaster.dto.CreateEventRequest;
import mx.uam.tsis.ticketmaster.dto.CreateEventRequest.CreateTicketTypeRequest;
import mx.uam.tsis.ticketmaster.dto.EventSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private TicketReservationRepository ticketReservationRepository;

    public EventDetailResponse createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setMaxTicketsPerPurchase(10);
        
        Event savedEvent = eventRepository.save(event);

        List<TicketType> ticketTypes = new ArrayList<>();
        for (CreateTicketTypeRequest ticketTypeRequest : request.getTicketTypes()) {
            TicketType ticketType = new TicketType();
            ticketType.setId(UUID.randomUUID().toString());
            ticketType.setName(ticketTypeRequest.getName());
            ticketType.setVenueZone(ticketTypeRequest.getVenueZone());
            ticketType.setPrice(ticketTypeRequest.getPrice());
            ticketType.setQuantity(ticketTypeRequest.getQuantity());
            ticketType.setAvailableQuantity(ticketTypeRequest.getQuantity());
            ticketType.setMaxPerPerson(ticketTypeRequest.getMaxPerPerson());
            ticketType.setSaleStartDate(ticketTypeRequest.getSaleStartDate());
            ticketType.setSaleEndDate(ticketTypeRequest.getSaleEndDate());
            ticketType.setEvent(savedEvent);
            ticketTypes.add(ticketTypeRepository.save(ticketType));
        }

        return getEventDetails(savedEvent.getId());
    }

    public EventDetailResponse getEventDetails(String eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
            
        List<TicketTypeInfo> ticketTypes = ticketTypeRepository.findByEvent(event)
            .stream()
            .map(this::mapToTicketTypeInfo)
            .collect(Collectors.toList());
            
        return new EventDetailResponse(
            event.getId(),
            event.getName(),
            event.getDescription(),
            event.getVenue(),
            event.getCategory(),
            event.getStartDate(),
            event.getEndDate(),
            ticketTypes
        );
    }

    public List<EventSummaryResponse> searchEvents(EventSearchRequest searchRequest) {
        List<Event> events = eventRepository.findAll();
        
        return events.stream()
            .filter(event -> matchesSearchCriteria(event, searchRequest))
            .map(event -> {
                List<TicketType> ticketTypes = ticketTypeRepository.findByEvent(event);
                List<TicketTypeInfo> ticketTypeInfos = ticketTypes.stream()
                    .map(this::mapToTicketTypeInfo)
                    .collect(Collectors.toList());

                int totalReservations = ticketTypes.stream()
                    .mapToInt(type -> ticketReservationRepository.findByTicketType_Id(type.getId()).size())
                    .sum();

                return new EventSummaryResponse(
                    event.getId(),
                    event.getName(),
                    event.getStartDate(),
                    event.getVenue(),
                    ticketTypeInfos,
                    totalReservations
                );
            })
            .collect(Collectors.toList());
    }

    private boolean matchesSearchCriteria(Event event, EventSearchRequest searchRequest) {
        if (searchRequest == null) {
            return true;
        }

        if (searchRequest.getName() != null && !event.getName().toLowerCase().contains(searchRequest.getName().toLowerCase())) {
            return false;
        }

        if (searchRequest.getCategory() != null && !event.getCategory().equals(searchRequest.getCategory())) {
            return false;
        }

        if (searchRequest.getMinPrice() != null || searchRequest.getMaxPrice() != null) {
            double minTicketPrice = ticketTypeRepository.findByEvent(event).stream()
                .mapToDouble(TicketType::getPrice)
                .min()
                .orElse(Double.MAX_VALUE);
            
            if (searchRequest.getMinPrice() != null && minTicketPrice < searchRequest.getMinPrice()) {
                return false;
            }
            
            if (searchRequest.getMaxPrice() != null && minTicketPrice > searchRequest.getMaxPrice()) {
                return false;
            }
        }

        if (Boolean.TRUE.equals(searchRequest.getHasAvailability())) {
            boolean hasAvailableTickets = ticketTypeRepository.findByEvent(event).stream()
                .anyMatch(ticketType -> ticketType.getAvailableQuantity() > 0);
            if (!hasAvailableTickets) {
                return false;
            }
        }

        return true;
    }

    public List<EventPromotionResponse> getPromotions() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findByStartDateAfter(now);
        
        return events.stream()
            .flatMap(event -> ticketTypeRepository.findByEvent(event).stream()
                .filter(ticketType -> {
                    LocalDateTime saleStart = ticketType.getSaleStartDate();
                    LocalDateTime saleEnd = ticketType.getSaleEndDate();
                    return now.isAfter(saleStart) && now.isBefore(saleEnd) &&
                           ticketType.getPromotionalPrice() != null &&
                           ticketType.getPromotionalPrice() < ticketType.getPrice();
                })
                .map(ticketType -> new EventPromotionResponse(
                    event.getId(),
                    event.getName(),
                    event.getDescription(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getVenue(),
                    event.getCategory(),
                    ticketType.getPrice(),
                    ticketType.getPromotionalPrice(),
                    ((ticketType.getPrice() - ticketType.getPromotionalPrice()) / ticketType.getPrice()) * 100,
                    ticketType.getSaleEndDate()
                ))
            )
            .collect(Collectors.toList());
    }
    
    private TicketTypeInfo mapToTicketTypeInfo(TicketType ticketType) {
        return new TicketTypeInfo(
            ticketType.getId(),
            ticketType.getName(),
            ticketType.getDescription(),
            ticketType.getPrice(),
            ticketType.getPromotionalPrice(),
            ticketType.getAvailableQuantity(),
            ticketType.getMaxPerPerson(),
            ticketType.getSaleStartDate(),
            ticketType.getSaleEndDate(),
            ticketType.getVenueZone()
        );
    }

    /**
     * Obtiene todos los eventos disponibles
     * @return Lista de eventos con sus detalles
     */
    public List<EventSummaryResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
            .map(event -> {
                List<TicketType> ticketTypes = ticketTypeRepository.findByEvent(event);
                
                // Obtener el total de reservas para este evento
                int totalReservations = ticketTypes.stream()
                    .mapToInt(type -> ticketReservationRepository.findByTicketType_Id(type.getId()).size())
                    .sum();

                return new EventSummaryResponse(
                    event.getId(),
                    event.getName(),
                    event.getStartDate(),
                    event.getVenue(),
                    ticketTypes.stream().map(this::mapToTicketTypeInfo).collect(Collectors.toList()),
                    totalReservations
                );
            })
            .collect(Collectors.toList());
    }

    public EventSummaryResponse getEventSummary(String eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        List<TicketType> ticketTypes = ticketTypeRepository.findByEvent(event);
        List<TicketTypeInfo> ticketTypeInfos = ticketTypes.stream()
            .map(this::mapToTicketTypeInfo)
            .collect(Collectors.toList());

        int totalReservations = ticketTypes.stream()
            .mapToInt(type -> ticketReservationRepository.findByTicketType_Id(type.getId()).size())
            .sum();

        return new EventSummaryResponse(
            event.getId(),
            event.getName(),
            event.getStartDate(),
            event.getVenue(),
            ticketTypeInfos,
            totalReservations
        );
    }
}
