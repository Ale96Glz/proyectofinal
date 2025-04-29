package mx.uam.tsis.ticketmaster.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import mx.uam.tsis.ticketmaster.negocio.TicketService;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketReservationResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketTypeDetailResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Tickets", description = "API para la gestión de tickets de eventos")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/types/{ticketTypeId}")
    @Operation(summary = "Obtener información de un tipo de ticket",
              description = "Retorna información detallada de un tipo de ticket, incluyendo disponibilidad y precios")
    public ResponseEntity<TicketTypeDetailResponse> getTicketTypeDetails(
            @PathVariable String ticketTypeId) {
        return ResponseEntity.ok(ticketService.getTicketTypeDetails(ticketTypeId));
    }

    @GetMapping("/event/{eventId}/types")
    @Operation(summary = "Obtener tipos de tickets disponibles para un evento",
              description = "Lista todos los tipos de tickets disponibles para un evento específico")
    public ResponseEntity<List<TicketTypeDetailResponse>> getTicketTypesForEvent(
            @Parameter(description = "ID del evento", required = true)
            @PathVariable String eventId) {
        return ResponseEntity.ok(ticketService.getTicketTypesForEvent(eventId));
    }
} 