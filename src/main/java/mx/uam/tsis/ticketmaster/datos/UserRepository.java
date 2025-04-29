package mx.uam.tsis.ticketmaster.datos;

import mx.uam.tsis.ticketmaster.negocio.modelo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
