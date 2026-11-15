package grupo3.ppai.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Usuarios")
@Setter
@Getter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String nombre;
    private String passwordHash;

    @OneToOne
    @JoinColumn(name = "idEmpleado")
    private Empleado empleado;

    protected Usuario() {
    }

    public Usuario(String nombre, String passwordHash, Empleado empleado) {
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.empleado = empleado;
    }

    public ComisionMedica getCMEmpleado() {
        return this.empleado.getComisionMedica();
    }
}
