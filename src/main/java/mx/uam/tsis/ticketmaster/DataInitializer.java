package mx.uam.tsis.ticketmaster;

import mx.uam.tsis.ticketmaster.datos.*;
import mx.uam.tsis.ticketmaster.negocio.modelo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(EventRepository eventRepository, 
                                 UserRepository userRepository,
                                 TicketTypeRepository ticketTypeRepository) {
        return args -> {
            // Create admin user
            User admin = new User();
            admin.setId("1");
            admin.setEmail("admin@ticketmaster.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.UserRole.ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);

            // Create sample event
            Event event = new Event();
            event.setId("1");
            event.setName("Sample Concert");
            event.setDescription("A sample concert event");
            event.setVenue("Sample Venue");
            event.setStartDate(LocalDateTime.now().plusDays(30));
            event.setEndDate(LocalDateTime.now().plusDays(30).plusHours(3));
            event.setCreatedBy(admin);
            event.setCreatedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());
            event.setStatus(Event.EventStatus.PUBLISHED);
            event.setCategory("Music");
            eventRepository.save(event);

            // Create ticket types
            TicketType vipTicket = new TicketType();
            vipTicket.setId("VIP"); // Using "VIP" as ID for easier access
            vipTicket.setName("VIP");
            vipTicket.setDescription("VIP access with meet & greet");
            vipTicket.setPrice(1000.0);
            vipTicket.setQuantity(100);
            vipTicket.setAvailableQuantity(100);
            vipTicket.setMaxPerPerson(10); // Aumentado para permitir probar el descuento por más de 5 tickets
            vipTicket.setSoldQuantity(0);
            vipTicket.setSaleStartDate(LocalDateTime.now());
            vipTicket.setSaleEndDate(LocalDateTime.now().plusDays(29));
            vipTicket.setVenueZone("VIP Zone");
            vipTicket.setEvent(event);
            ticketTypeRepository.save(vipTicket);

            TicketType generalTicket = new TicketType();
            generalTicket.setId("GENERAL");
            generalTicket.setName("General");
            generalTicket.setDescription("General admission");
            generalTicket.setPrice(200.0);
            generalTicket.setQuantity(500);
            generalTicket.setAvailableQuantity(500);
            generalTicket.setMaxPerPerson(8);
            generalTicket.setSoldQuantity(0);
            generalTicket.setSaleStartDate(LocalDateTime.now());
            generalTicket.setSaleEndDate(LocalDateTime.now().plusDays(29));
            generalTicket.setVenueZone("General Zone");
            generalTicket.setEvent(event);
            ticketTypeRepository.save(generalTicket);
        };
    }
}
