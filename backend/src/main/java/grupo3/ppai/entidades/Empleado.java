package grupo3.ppai.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public ComisionMedica getComisionMedica() {
        return comisionMedica;
    }

    public void setComisionMedica(ComisionMedica comisionMedica) {
        this.comisionMedica = comisionMedica;
    }

    public ComisionMedica getCM() {
        return comisionMedica;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

