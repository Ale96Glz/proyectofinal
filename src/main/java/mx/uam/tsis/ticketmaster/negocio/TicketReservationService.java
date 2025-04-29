package mx.uam.tsis.ticketmaster.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.uam.tsis.ticketmaster.datos.EventRepository;
import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.datos.SeatRepository;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.Seat;
import mx.uam.tsis.ticketmaster.dto.ApiResponses;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketReservationResponse;
import mx.uam.tsis.ticketmaster.dto.ApiResponses.SeatAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketReservationService {
    private static final int RESERVATION_TIMEOUT_MINUTES = 5;
    
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketReservationRepository reservationRepository;
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private SeatRepository seatRepository;
    
    /**
     * Crea una nueva reservación de tickets
     * @param eventId ID del evento
     * @param ticketTypeId ID del tipo de ticket
     * @param quantity Cantidad de tickets a reservar
     * @param seatIds IDs de los asientos específicos (opcional)
     * @return Respuesta con los detalles de la reservación
     */
    @Transactional
    public TicketReservationResponse createReservation(String eventId, String ticketTypeId, int quantity, Set<String> seatIds) {
        // Validar que el evento exista y esté activo
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
            
        if (!event.isActive()) {
            throw new IllegalStateException("El evento no está disponible para reservaciones");
        }

        // Validar que el tipo de ticket pertenezca al evento
        TicketType ticketType = ticketTypeRepository.findByIdAndEventId(ticketTypeId, eventId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de entrada no encontrado para este evento"));
            
        // Validar fecha del evento
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El evento ya ha pasado");
        }

        // Validar que la cantidad sea válida
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // Validar que no exceda el máximo de 10 boletos por reservación
        if (quantity > 10) {
            throw new IllegalArgumentException("No se pueden comprar más de 10 boletos por reservación");
        }

        if (quantity > event.getMaxTicketsPerPurchase()) {
            throw new IllegalArgumentException("Excede el máximo de tickets permitidos por compra");
        }

        // Validar disponibilidad
        synchronized(ticketType) {
            if (ticketType.getAvailableQuantity() < quantity) {
                throw new IllegalStateException("No hay suficientes entradas disponibles");
            }

            // Validar asientos
            if (seatIds != null && !seatIds.isEmpty()) {
                if (seatIds.size() != quantity) {
                    throw new IllegalArgumentException("La cantidad de asientos no coincide con la cantidad de tickets");
                }

                Set<Seat> seats = seatRepository.findAllById(seatIds)
                    .stream()
                    .collect(Collectors.toSet());

                if (seats.size() != seatIds.size()) {
                    throw new IllegalArgumentException("Uno o más asientos no existen");
                }

                if (seats.stream().anyMatch(seat -> !seat.isAvailable() || !seat.getTicketType().equals(ticketType))) {
                    throw new IllegalStateException("Uno o más asientos no están disponibles o no pertenecen a este tipo de ticket");
                }

                // Reservar asientos
                seats.forEach(seat -> {
                    seat.setAvailable(false);
                    seatRepository.save(seat);
                });
            }
            
            // Calcular precio total con descuento si aplica
            double pricePerTicket = ticketType.getPrice();
            double totalPrice = pricePerTicket * quantity;
            
            if (quantity > 5) {
                totalPrice = totalPrice * 0.9; // 10% de descuento
            }
            
            // Crear reservación
            TicketReservation reservation = new TicketReservation();
            reservation.setId(UUID.randomUUID().toString());
            reservation.setTicketType(ticketType);
            reservation.setQuantity(quantity);
            reservation.setTotalPrice(totalPrice);
            reservation.setActive(true);
            reservation.setExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES));
            
            // Actualizar disponibilidad
            ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() - quantity);
            ticketTypeRepository.save(ticketType);
            
            // Guardar reservación
            reservation = reservationRepository.save(reservation);
            
            return new TicketReservationResponse(
                reservation.getId(),
                ticketType.getId(),
                ticketType.getName(),
                ticketType.getVenueZone(),
                quantity,
                pricePerTicket,
                totalPrice,
                quantity > 5,
                reservation.getExpiresAt(),
                reservation.getActive()
            );
        }
    }
    
    @Transactional
    public void cancelReservation(String reservationId) {
        TicketReservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservación no encontrada"));
            
        if (!reservation.getActive()) {
            return;
        }
        
        // Devolver tickets al inventario
        TicketType ticketType = reservation.getTicketType();
        synchronized(ticketType) {
            ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() + reservation.getQuantity());
            ticketTypeRepository.save(ticketType);
            
            // Eliminar la reservación en lugar de marcarla como inactiva
            reservationRepository.delete(reservation);
        }
    }
    
    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos en lugar de cada minuto
    @Transactional
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minimumAge = now.minusMinutes(1); // No procesar reservaciones más nuevas que 1 minuto
        
        List<TicketReservation> expiredReservations = 
            reservationRepository.findByActiveAndExpiresAtBefore(true, now)
                .stream()
                .filter(reservation -> reservation.getExpiresAt().isBefore(minimumAge))
                .toList();
            
        for (TicketReservation reservation : expiredReservations) {
            cancelReservation(reservation.getId());
        }
    }

    public List<SeatAvailabilityResponse> getAvailableSeats(String eventId, String ticketTypeId) {
        // Validar que el evento exista y esté activo
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
            
        if (!event.isActive()) {
            throw new IllegalStateException("El evento no está disponible para consulta");
        }

        // Validar que el tipo de ticket pertenezca al evento
        TicketType ticketType = ticketTypeRepository.findByIdAndEventId(ticketTypeId, eventId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de entrada no encontrado para este evento"));

        // Validar fecha del evento
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El evento ya ha pasado");
        }

        return seatRepository.findByTicketTypeAndAvailable(ticketType, true)
            .stream()
            .map(seat -> new SeatAvailabilityResponse(
                seat.getId(),
                seat.getSeatRow(),
                seat.getNumber(),
                seat.getZone(),
                ticketType.getPrice()
            ))
            .toList();
    }

    public List<TicketReservationResponse> getAllReservations() {
        List<TicketReservation> reservations = reservationRepository.findAll();
        return reservations.stream()
            .map(this::mapToResponse)
            .toList();
    }

    public List<TicketReservationResponse> getActiveReservations() {
        List<TicketReservation> activeReservations = 
            reservationRepository.findByActiveAndExpiresAtAfter(true, LocalDateTime.now());
        return activeReservations.stream()
            .map(this::mapToResponse)
            .toList();
    }

    private TicketReservationResponse mapToResponse(TicketReservation reservation) {
        TicketType ticketType = reservation.getTicketType();
        return new TicketReservationResponse(
            reservation.getId(),
            ticketType.getId(),
            ticketType.getName(),
            ticketType.getVenueZone(),
            reservation.getQuantity(),
            ticketType.getPrice(),
            reservation.getTotalPrice(),
            reservation.getQuantity() > 5,
            reservation.getExpiresAt(),
            reservation.getActive()
        );
    }
}
