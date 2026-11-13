package grupo3.ppai.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ComisionesMedicas")
@Setter
@Getter
public class ComisionMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private Integer codigo;
    private String direccion;
    private String email;
    private String telefono;
    private String nombre;

    protected ComisionMedica() {
    }

    public ComisionMedica(Integer codigo, String direccion, String email, String telefono, String nombre) {
        this.codigo = codigo;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.nombre = nombre;
    }
}
