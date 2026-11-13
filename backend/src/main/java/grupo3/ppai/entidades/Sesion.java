package grupo3.ppai.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Sesiones")
@Setter
@Getter
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraExpiracion;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    protected Sesion() {
    }

    public Sesion(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraExpiracion, Usuario usuario) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraExpiracion = fechaHoraExpiracion;
        this.usuario = usuario;
    }

    public ComisionMedica getCMUsuario() {
        return this.usuario.getCMEmpleado();
    }
}
