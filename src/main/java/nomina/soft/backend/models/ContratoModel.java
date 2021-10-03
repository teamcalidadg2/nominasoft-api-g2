package nomina.soft.backend.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "contrato")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContratoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idContrato;

    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin; 
    @Getter @Setter private Boolean tieneAsignacionFamiliar;
    @Getter @Setter private String horasPorSemana;
    @Getter @Setter private String pagoPorHora;
    @Getter @Setter private String puesto;
    @Getter @Setter private Boolean estaCancelado;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    @Getter @Setter private EmpleadoModel empleado;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_afp")
    @Getter @Setter private AfpModel afp;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="contrato")
    @JsonIgnore
	@Getter @Setter private List<IncidenciaLaboralModel> incidenciasLaborales;

    public void addIncidenciaLaboral(IncidenciaLaboralModel nuevaIncidenciaLaboral) {
        incidenciasLaborales.add(nuevaIncidenciaLaboral);
    }
}
