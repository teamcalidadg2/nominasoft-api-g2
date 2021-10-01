package nomina.soft.backend.dto;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.NominaModel;

@AllArgsConstructor
@NoArgsConstructor
public class PeriodoNominaDto {
    @Getter @Setter private Long idPeriodoNomina;
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin;
	@Getter @Setter private List<IncidenciaLaboralModel> incidenciasLaborales;
	@Getter @Setter private List<NominaModel> nominas;
}
