package mx.uam.tsis.ticketmaster.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import mx.uam.tsis.ticketmaster.dto.ApiResponses.TicketTypeDetailResponse;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketReservation;
import mx.uam.tsis.ticketmaster.negocio.modelo.TicketType;
import mx.uam.tsis.ticketmaster.negocio.modelo.Event;
import mx.uam.tsis.ticketmaster.datos.TicketRepository;
import mx.uam.tsis.ticketmaster.datos.TicketTypeRepository;
import mx.uam.tsis.ticketmaster.datos.TicketReservationRepository;
import mx.uam.tsis.ticketmaster.datos.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    
    @Autowired
    private TicketReservationRepository ticketReservationRepository;

    @Autowired
    private EventRepository eventRepository;

    public TicketTypeDetailResponse getTicketTypeDetails(String ticketTypeId) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de ticket no encontrado"));
            
        return new TicketTypeDetailResponse(
            ticketType.getId(),
            ticketType.getName(),
            ticketType.getDescription(),
            ticketType.getPrice(),
            ticketType.getAvailableQuantity(),
            ticketType.getMaxPerPerson(),
            ticketType.getVenueZone(),
            ticketType.getSaleStartDate(),
            ticketType.getSaleEndDate()
        );
    }

    public List<TicketTypeDetailResponse> getTicketTypesForEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        List<TicketType> ticketTypes = ticketTypeRepository.findByEvent(event);
        return ticketTypes.stream()
            .map(ticketType -> new TicketTypeDetailResponse(
                ticketType.getId(),
                ticketType.getName(),
                ticketType.getDescription(),
                ticketType.getPrice(),
                ticketType.getAvailableQuantity(),
                ticketType.getMaxPerPerson(),
                ticketType.getVenueZone(),
                ticketType.getSaleStartDate(),
                ticketType.getSaleEndDate()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public TicketReservation selectTickets(String eventId, String ticketTypeId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        TicketType ticketType = ticketTypeRepository.findByIdAndEvent(ticketTypeId, event)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de ticket no válido"));

        // Validar que estamos dentro del período de venta
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ticketType.getSaleStartDate())) {
            throw new IllegalStateException("La venta de tickets aún no ha comenzado");
        }
        if (now.isAfter(ticketType.getSaleEndDate())) {
            throw new IllegalStateException("La venta de tickets ha finalizado");        
        }

        // Validar que el evento no haya pasado
        if (event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El evento ya ha pasado");
        }

        // Validar límite por persona
        if (quantity > ticketType.getMaxPerPerson()) {
            throw new IllegalArgumentException(
                "La cantidad excede el límite permitido por persona: " + 
                ticketType.getMaxPerPerson()
            );
        }

        // Validar disponibilidad
        if (quantity > ticketType.getAvailableQuantity()) {
            throw new IllegalArgumentException("No hay suficientes entradas disponibles");
        }

        // Calcular precio total con descuento si aplica
        double pricePerTicket = ticketType.getPrice();
        double totalPrice = pricePerTicket * quantity;
        boolean applyDiscount = quantity > 5;
        
        if (applyDiscount) {
            totalPrice = totalPrice * 0.9; // 10% de descuento por más de 5 tickets
        }

        // Crear reservación
        TicketReservation reservation = new TicketReservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setTicketType(ticketType);
        reservation.setQuantity(quantity);
        reservation.setTotalPrice(totalPrice);
        reservation.setActive(true);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Actualizar inventario
        synchronized(ticketType) {
            ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() - quantity);
            ticketTypeRepository.save(ticketType);
        }

        return ticketReservationRepository.save(reservation);
    }
    
    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    @Transactional
    public void cleanupExpiredReservations() {
        List<TicketReservation> expiredReservations = 
            ticketReservationRepository.findByActiveAndExpiresAtBefore(true, LocalDateTime.now());
            
        for (TicketReservation reservation : expiredReservations) {
            // Restaurar tickets al inventario
            TicketType ticketType = reservation.getTicketType();
            ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() + reservation.getQuantity()
            );
            ticketTypeRepository.save(ticketType);
            
            // Marcar reserva como inactiva
            reservation.setActive(false);
            ticketReservationRepository.save(reservation);
        }
    }
}
