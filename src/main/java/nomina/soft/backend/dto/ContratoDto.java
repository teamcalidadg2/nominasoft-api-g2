package nomina.soft.backend.dto;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.IncidenciaLaboralModel;

@AllArgsConstructor
@NoArgsConstructor
public class ContratoDto {
    @Getter @Setter
    private int contrato_id;
    @Getter @Setter
    private String nombres;
    @Getter @Setter
    private Date fechaInicio;
    @Getter @Setter
    private Date fechaFin; 
    @Getter @Setter
    private Boolean tieneAsignacionFamiliar;
    @Getter @Setter
    private String horasPorSemana;
    @Getter @Setter
    private String pagoPorHora;
    @Getter @Setter
    private String puesto;
    @Getter @Setter
    private Boolean cancelado;
    @Getter @Setter
    private int empleado_id;
    @Getter @Setter
    private int afp_id;
	@Getter @Setter
	private Set<IncidenciaLaboralModel> incidenciaLaborales;



}
