package grupo3.ppai.entidades;

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
@Table(name = "Empleados")
@Setter
@Getter
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String apellido;
    private String nombre;
    private String email;

    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "idComisionesMedica")
    private ComisionMedica comisionMedica;

    protected Empleado() {
    }

    public Long getId() {
        return id;
    }

    public Empleado(String apellido, String nombre, String email, Rol rol, ComisionMedica comisionMedica) {
        this.apellido = apellido;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.comisionMedica = comisionMedica;
    }

    public Empleado sosGCM() {
        if (this.rol.esGCM()) {
            return this;
        } else {
            return null;
        }
    }
    ;

    public boolean esTuCMCMDestinoBolsin(String nombreCMDestinoBolsin) {
        if (this.comisionMedica.getNombre().equals(nombreCMDestinoBolsin)) {
            return true;
        } else {
            return false;
        }
    }
}

