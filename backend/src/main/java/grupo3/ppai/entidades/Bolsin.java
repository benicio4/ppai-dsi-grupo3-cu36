package grupo3.ppai.entidades;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Bolsines")
public class Bolsin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroBolsin;
    private Integer numeroPrecinto;
    private Double peso;
    private LocalDate fecha;
    private LocalTime hora;

    // Asociacion "origen" (1) con ComisionMedica
    @ManyToOne
    @JoinColumn(name = "idComisionMedicaOrigen")
    private ComisionMedica origen;

    // Asociacion "destino" (1) con ComisionMedica
    @ManyToOne
    @JoinColumn(name = "idComisionMedicaDestino")
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idBolsin")
    private List<CambioEstadoBolsin> cambiosEstado = new ArrayList<>();

    protected Bolsin() {
    }

    public Bolsin(Integer numeroBolsin, Integer numeroPrecinto, Double peso, LocalDate fecha, LocalTime hora,
            ComisionMedica origen, ComisionMedica destino) {
        this.numeroBolsin = numeroBolsin;
        this.numeroPrecinto = numeroPrecinto;
        this.peso = peso;
        this.fecha = fecha;
        this.hora = hora;
        this.origen = origen;
        this.destino = destino;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumeroBolsin() {
        return numeroBolsin;
    }

    public void setNumeroBolsin(Integer numeroBolsin) {
        this.numeroBolsin = numeroBolsin;
    }

    public Integer getNumeroPrecinto() {
        return numeroPrecinto;
    }

    public void setNumeroPrecinto(Integer numeroPrecinto) {
        this.numeroPrecinto = numeroPrecinto;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public ComisionMedica getOrigen() {
        return origen;
    }

    public void setOrigen(ComisionMedica origen) {
        this.origen = origen;
    }

    public ComisionMedica getDestino() {
        return destino;
    }

    public void setDestino(ComisionMedica destino) {
        this.destino = destino;
    }

    // ---- Metodos del CU 36 ----

    public boolean esTuCMOrigenCMDeUsuario(ComisionMedica cmUsuario) {
        return this.origen != null && this.origen.getId().equals(cmUsuario.getId());
    }

    public String conocerCMDestino() {
        return this.destino.getNombre();
    }

     public boolean conocerCambioEstado() {
         for (CambioEstadoBolsin cambio : cambiosEstado) {
             if (cambio.sosActual()) {
                 return cambio.sosEnviado();
            }
        }
        return false;
     }
}
