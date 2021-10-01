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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "periodo_nomina")
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoNominaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idPeriodoNomina;

    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="periodoNomina")
    @JsonIgnore
	@Getter @Setter private List<IncidenciaLaboralModel> incidenciasLaborales;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="periodoNomina")
    @JsonIgnore
	@Getter @Setter private List<NominaModel> nominas;

    public void addIncidenciaLaboral(IncidenciaLaboralModel nuevaIncidenciaLaboral) {
        incidenciasLaborales.add(nuevaIncidenciaLaboral);
    }

}
