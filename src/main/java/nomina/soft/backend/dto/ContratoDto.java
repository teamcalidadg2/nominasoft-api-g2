package nomina.soft.backend.dto;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.IncidenciaLaboralModel;

@AllArgsConstructor
@NoArgsConstructor
public class ContratoDto {
    @Getter @Setter private Long idContrato;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin; 
    @Getter @Setter private Boolean tieneAsignacionFamiliar;
    @Getter @Setter private String horasPorSemana;
    @Getter @Setter private String pagoPorHora;
    @Getter @Setter private String puesto;
    @Getter @Setter private Boolean estaCancelado;
    @Getter @Setter private Long idEmpleado;
    @Getter @Setter private Long idAfp;
	@Getter @Setter private List<IncidenciaLaboralModel> incidenciasLaborales;
}
