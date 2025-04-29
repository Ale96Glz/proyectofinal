package mx.uam.tsis.ticketmaster.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.uam.tsis.ticketmaster.negocio.TicketReservationService;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketReservationResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.SeatAvailabilityResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservaciones", description = "API para gestionar reservaciones de tickets")
public class TicketReservationController {
    
    @Autowired
    private TicketReservationService reservationService;

    @GetMapping
    @Operation(
        summary = "Listar todas las reservaciones",
        description = "Obtiene una lista de todas las reservaciones activas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de reservaciones obtenida exitosamente")
    public ResponseEntity<List<TicketReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/active")
    @Operation(
        summary = "Listar reservaciones activas",
        description = "Obtiene una lista de las reservaciones que aún no han expirado"
    )
    @ApiResponse(responseCode = "200", description = "Lista de reservaciones activas obtenida exitosamente")
    public ResponseEntity<List<TicketReservationResponse>> getActiveReservations() {
        return ResponseEntity.ok(reservationService.getActiveReservations());
    }
    
    @GetMapping("/events/{eventId}/seats/{ticketTypeId}")
    @Operation(
        summary = "Obtener asientos disponibles",
        description = "Obtiene la lista de asientos disponibles para un tipo de ticket en un evento específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de asientos obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Evento o tipo de ticket no encontrado"),
        @ApiResponse(responseCode = "409", description = "El evento ya pasó o no está disponible")
    })
    public ResponseEntity<List<SeatAvailabilityResponse>> getAvailableSeats(
            @Parameter(description = "ID del evento", required = true)
            @PathVariable String eventId,
            @Parameter(description = "ID del tipo de ticket", required = true)
            @PathVariable String ticketTypeId) {
        try {
            return ResponseEntity.ok(reservationService.getAvailableSeats(eventId, ticketTypeId));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping
    @Operation(
        summary = "Crear una nueva reservación",
        description = "Crea una reservación temporal de tickets por 5 minutos. Si se reservan más de 5 tickets, se aplica un descuento del 10%."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservación creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Evento o tipo de ticket no encontrado"),
        @ApiResponse(responseCode = "409", description = "No hay suficientes tickets disponibles o el evento ya pasó")
    })
    public ResponseEntity<?> createReservation(
            @Parameter(description = "ID del evento", required = true) 
            @RequestParam String eventId,
            @Parameter(description = "ID del tipo de ticket a reservar", required = true) 
            @RequestParam String ticketTypeId,
            @Parameter(description = "Cantidad de tickets a reservar", required = true) 
            @RequestParam Integer quantity,
            @Parameter(description = "IDs de los asientos a reservar (opcional)") 
            @RequestParam(required = false) Set<String> seatIds) {
        try {
            TicketReservationResponse response = reservationService.createReservation(eventId, ticketTypeId, quantity, seatIds);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Errores de validación de datos
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            // Incluir el mensaje de error en la respuesta
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            // Errores de estado (disponibilidad, evento pasado, etc)
            return ResponseEntity.status(409).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{reservationId}")
    @Operation(
        summary = "Cancelar una reservación",
        description = "Cancela una reservación existente y libera los tickets reservados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservación cancelada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reservación no encontrada")
    })
    public ResponseEntity<Map<String, String>> cancelReservation(
            @Parameter(description = "ID de la reservación a cancelar", required = true)
            @PathVariable String reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reservación cancelada exitosamente");
            response.put("reservationId", reservationId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 