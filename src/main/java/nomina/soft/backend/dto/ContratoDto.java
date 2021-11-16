package nomina.soft.backend.dto;
import static nomina.soft.backend.servicios.Utility.TIME_ZONE;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nomina.soft.backend.entidades.IncidenciaLaboral;
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContratoDto {
    @Getter @Setter private Long idContrato;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone=TIME_ZONE)
    @Getter @Setter private Date fechaInicio;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone=TIME_ZONE)
    @Getter @Setter private Date fechaFin; 
    @Getter @Setter private Boolean tieneAsignacionFamiliar;
    @Getter @Setter private String horasPorSemana;
    @Getter @Setter private String pagoPorHora;
    @Getter @Setter private String puesto;
    @Getter @Setter private Boolean estaCancelado;
    @Getter @Setter private String idEmpleado;
    @Getter @Setter private String idAfp;
	@Getter @Setter private List<IncidenciaLaboral> incidenciasLaborales;


}
