package nomina.soft.backend.dto;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;

@AllArgsConstructor
@NoArgsConstructor
public class ContratoDto {
    @Getter @Setter
    private int contrato_id;
    @Getter @Setter
    private String nombres;
    @Getter @Setter
    private LocalDateTime fechaInicio;
    @Getter @Setter
    private LocalDateTime fechaFin; 
    @Getter @Setter
    private Boolean tieneAsignacionFamiliar;
    @Getter @Setter
    private int horasPorSemana;
    @Getter @Setter
    private Double pagoPorHora;
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
