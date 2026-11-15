package grupo3.ppai.repositorios;

import java.time.LocalDateTime;
import java.util.Optional;

import grupo3.ppai.entidades.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    // Sesion vigente mas reciente (mientras no exista login, es la "actualSesion" del Gestor)
    Optional<Sesion> findFirstByFechaHoraExpiracionAfterOrderByFechaHoraInicioDesc(LocalDateTime ahora);
}
