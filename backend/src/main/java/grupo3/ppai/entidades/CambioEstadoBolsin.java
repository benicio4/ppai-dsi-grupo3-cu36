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

/**
 * CambioEstadoBolsin en la vista estática.
 * La FK bolsinId se mapea desde Bolsin (Bolsin conoce 1..* cambioEstado)
 * con @OneToMany @JoinColumn(name = "bolsinId").
 */
@Entity
@Table(name = "CambiosEstadosBolsines")
@Setter
@Getter
public class CambioEstadoBolsin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "estadoId")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "responsableId")
    private Empleado responsable;

    protected CambioEstadoBolsin() {
    }

    public CambioEstadoBolsin(LocalDateTime fechaInicio, Estado estado, Empleado responsable) {
        this.fechaInicio = fechaInicio;
        this.estado = estado;
        this.responsable = responsable;
    }

    public Estado conocerEstado() {
        return this.estado;
    }

    public boolean sosActual() {
        return this.fechaFin == null;
    }

    public boolean sosEnviado() {
        return this.estado.esEnviado();
    }
}
