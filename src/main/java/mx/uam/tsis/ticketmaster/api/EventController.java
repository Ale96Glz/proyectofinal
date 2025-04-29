package mx.uam.tsis.ticketmaster.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.uam.tsis.ticketmaster.negocio.EventService;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.*;
import mx.uam.tsis.ticketmaster.dto.CreateEventRequest;
import mx.uam.tsis.ticketmaster.dto.EventSearchRequest;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "API para la gestión de eventos")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    @Operation(summary = "Crear un nuevo evento", 
              description = "Crea un nuevo evento con la información proporcionada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EventDetailResponse> createEvent(
            @RequestBody CreateEventRequest eventRequest) {
        return ResponseEntity.ok(eventService.createEvent(eventRequest));
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Obtener detalles de un evento", 
              description = "Retorna los detalles completos de un evento, incluyendo tipos de tickets disponibles")
    public ResponseEntity<EventDetailResponse> getEventDetails(
            @PathVariable String eventId) {
        return ResponseEntity.ok(eventService.getEventDetails(eventId));
    }

    @GetMapping
    @Operation(summary = "Listar todos los eventos", 
              description = "Retorna una lista de todos los eventos disponibles con sus detalles")
    public ResponseEntity<List<EventSummaryResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/search")
    @Operation(summary = "Búsqueda de eventos",
              description = "Permite buscar eventos con múltiples criterios como fecha, precio, categoría, etc.")
    public ResponseEntity<List<EventSummaryResponse>> searchEvents(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String query,
            @Parameter(description = "Categoría del evento")
            @RequestParam(required = false) String category,
            @Parameter(description = "Venue/Lugar del evento")
            @RequestParam(required = false) String venue,
            @Parameter(description = "Fecha desde (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) LocalDateTime fromDate,
            @Parameter(description = "Fecha hasta (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) LocalDateTime toDate,
            @Parameter(description = "Precio mínimo")
            @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Precio máximo")
            @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Solo eventos con tickets disponibles")
            @RequestParam(required = false) Boolean hasAvailability) {
        
        EventSearchRequest searchRequest = new EventSearchRequest();
        searchRequest.setQuery(query);
        searchRequest.setCategory(category);
        searchRequest.setVenue(venue);
        searchRequest.setFromDate(fromDate);
        searchRequest.setToDate(toDate);
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setHasAvailability(hasAvailability);
        
        return ResponseEntity.ok(eventService.searchEvents(searchRequest));
    }

    @GetMapping("/promotions")
    @Operation(summary = "Obtener eventos en promoción", 
              description = "Retorna eventos con promociones activas")
    public ResponseEntity<List<EventPromotionResponse>> getPromotions() {
        return ResponseEntity.ok(eventService.getPromotions());
    }
} 