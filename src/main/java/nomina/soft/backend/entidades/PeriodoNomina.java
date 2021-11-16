package nomina.soft.backend.entidades;
import static nomina.soft.backend.statics.PeriodoNominaImplConstant.FECHAS_NO_VALIDAS;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "periodo_nomina")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PeriodoNomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idPeriodoNomina;

    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="periodoNomina")
    @JsonIgnore
	@Getter @Setter private List<IncidenciaLaboral> incidenciasLaborales;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy="periodoNomina")
    @JsonIgnore
	@Getter @Setter private List<Nomina> nominas;

    public void addIncidenciaLaboral(IncidenciaLaboral nuevaIncidenciaLaboral) {
        incidenciasLaborales.add(nuevaIncidenciaLaboral);
    }

    public boolean fechasValidas(Date fechaInicioPeriodoNomina, Date fechaFinPeriodoNomina) //REGLA 13
                                throws PeriodoNominaNotValidException{
		int duracionDias = Period.between(LocalDate.ofInstant(fechaInicioPeriodoNomina.toInstant(), ZoneId.of("America/Lima")),
										LocalDate.ofInstant(fechaFinPeriodoNomina.toInstant(), ZoneId.of("America/Lima")))
										.getDays();
		if(!(duracionDias>15 && duracionDias<30))   throw new PeriodoNominaNotValidException(FECHAS_NO_VALIDAS);
		return true;								
	}


}
